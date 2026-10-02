package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.exception.FileStorageException;
import com.matrimony.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Stores uploads on the local disk (app.file-storage.location), served at /files/**. Used for local development.
 */
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.file-storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalFileStorageService implements FileStorageService {
    private final AppProperties properties;

    @Override
    public StoredFile storeProfilePhoto(MultipartFile file, String matrimonyId) {
        return store(file, FileStorageSupport.photoKey(properties, file, matrimonyId));
    }

    @Override
    public StoredFile storeDocument(MultipartFile file, String matrimonyId) {
        return store(file, FileStorageSupport.documentKey(properties, file, matrimonyId));
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(Path.of(properties.fileStorage().location()).resolve(storageKey).normalize());
        } catch (IOException ex) {
            throw new FileStorageException("Unable to delete file");
        }
    }

    private StoredFile store(MultipartFile file, String storageKey) {
        Path target = Path.of(properties.fileStorage().location()).resolve(storageKey).normalize();
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target);
        } catch (IOException ex) {
            throw new FileStorageException("Unable to store file");
        }
        String url = "/files/" + storageKey.replace('\\', '/');
        return new StoredFile(storageKey, url, url);
    }
}
