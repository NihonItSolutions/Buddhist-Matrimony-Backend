package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.exception.FileStorageException;
import com.matrimony.backend.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Map;

/**
 * Stores uploads in Supabase Storage.
 * Profile and success-story photos go to a public bucket and are returned as direct CDN URLs.
 * Documents (Aadhaar etc.) go to a private bucket and are reached through /files/** with a short-lived signed URL.
 */
@Slf4j
@Service
@ConditionalOnProperty(name = "app.file-storage.provider", havingValue = "supabase")
public class SupabaseStorageService implements FileStorageService {
    private static final int SIGNED_URL_SECONDS = 600;

    private final AppProperties properties;
    private final RestClient client;
    private final String baseUrl;

    public SupabaseStorageService(AppProperties properties) {
        this.properties = properties;
        AppProperties.FileStorage cfg = properties.fileStorage();
        if (isBlank(cfg.supabaseUrl()) || isBlank(cfg.supabaseServiceKey())) {
            throw new IllegalStateException("SUPABASE_URL and SUPABASE_SERVICE_KEY must be set when FILE_STORAGE_PROVIDER=supabase");
        }
        this.baseUrl = cfg.supabaseUrl().replaceAll("/+$", "");
        this.client = RestClient.builder()
                .defaultHeader("apikey", cfg.supabaseServiceKey())
                .defaultHeader("Authorization", "Bearer " + cfg.supabaseServiceKey())
                .build();
    }

    @Override
    public StoredFile storeProfilePhoto(MultipartFile file, String matrimonyId) {
        String key = FileStorageSupport.photoKey(properties, file, matrimonyId);
        upload(publicBucket(), key, file);
        String url = publicUrl(key);
        return new StoredFile(key, url, url);
    }

    @Override
    public StoredFile storeDocument(MultipartFile file, String matrimonyId) {
        String key = FileStorageSupport.documentKey(properties, file, matrimonyId);
        upload(privateBucket(), key, file);
        String url = "/files/" + key;
        return new StoredFile(key, url, url);
    }

    @Override
    public void delete(String storageKey) {
        try {
            client.delete().uri(objectUri("object", bucketFor(storageKey), storageKey)).retrieve().toBodilessEntity();
        } catch (RestClientException ex) {
            log.warn("Could not delete {} from Supabase Storage: {}", storageKey, ex.getMessage());
        }
    }

    /** Where /files/{key} should send the browser: the public CDN URL, or a short-lived signed URL for private files. */
    public String resolve(String storageKey) {
        if (storageKey.startsWith("documents/")) {
            Map<?, ?> body = client.post().uri(objectUri("object/sign", privateBucket(), storageKey))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("expiresIn", SIGNED_URL_SECONDS))
                    .retrieve().body(Map.class);
            Object signed = body == null ? null : body.get("signedURL");
            if (signed == null) {
                throw new FileStorageException("Unable to create a link for this file");
            }
            return baseUrl + "/storage/v1" + signed;
        }
        return publicUrl(storageKey);
    }

    private void upload(String bucket, String key, MultipartFile file) {
        try {
            client.post().uri(objectUri("object", bucket, key))
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header("x-upsert", "true")
                    .body(file.getBytes())
                    .retrieve().toBodilessEntity();
        } catch (IOException | RestClientException ex) {
            log.error("Upload of {} to Supabase Storage failed", key, ex);
            throw new FileStorageException("Unable to store file");
        }
    }

    // Built by hand so the "/" separators inside storage keys are not encoded as %2F.
    private URI objectUri(String action, String bucket, String key) {
        return URI.create(baseUrl + "/storage/v1/" + action + "/" + bucket + "/" + key);
    }

    private String publicUrl(String key) {
        return baseUrl + "/storage/v1/object/public/" + publicBucket() + "/" + key;
    }

    private String bucketFor(String key) {
        return key.startsWith("documents/") ? privateBucket() : publicBucket();
    }

    private String publicBucket() {
        return properties.fileStorage().publicBucket();
    }

    private String privateBucket() {
        return properties.fileStorage().privateBucket();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
