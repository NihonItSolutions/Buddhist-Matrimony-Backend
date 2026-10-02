package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.exception.FileStorageException;
import com.matrimony.backend.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LocalFileStorageService implements FileStorageService {
    private final AppProperties properties;

    @Override
    public StoredFile storeProfilePhoto(MultipartFile file, String matrimonyId) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }
        if (file.getSize() > properties.fileStorage().maxPhotoSizeBytes()) {
            throw new FileStorageException("File size exceeds configured maximum");
        }
        String contentType = file.getContentType();
        if (contentType == null || !properties.fileStorage().allowedPhotoContentTypes().contains(contentType)) {
            throw new FileStorageException("Unsupported photo content type");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "photo" : file.getOriginalFilename());
        String extension = extension(original, contentType);
        String storageKey = "profiles/" + matrimonyId + "/" + UUID.randomUUID() + extension;
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

    @Override
    public StoredFile storeDocument(MultipartFile file, String matrimonyId) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }
        if (file.getSize() > properties.fileStorage().maxPhotoSizeBytes()) {
            throw new FileStorageException("File size exceeds configured maximum");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(properties.fileStorage().allowedPhotoContentTypes().contains(contentType) || contentType.equals("application/pdf"))) {
            throw new FileStorageException("Unsupported document content type. Must be PDF or image.");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "document" : file.getOriginalFilename());
        String extension = extension(original, contentType);
        String storageKey = "documents/" + matrimonyId + "/" + UUID.randomUUID() + extension;
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

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(Path.of(properties.fileStorage().location()).resolve(storageKey).normalize());
        } catch (IOException ex) {
            throw new FileStorageException("Unable to delete file");
        }
    }

    private String extension(String filename, String contentType) {
        int dot = filename.lastIndexOf('.');
        if (dot > -1 && dot < filename.length() - 1) {
            String ext = filename.substring(dot).toLowerCase(Locale.ROOT);
            if (ext.matches("\\.(jpg|jpeg|png|webp)$")) {
                return ext;
            }
        }
        return switch (contentType) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "application/pdf" -> ".pdf";
            default -> ".jpg";
        };
    }
}
