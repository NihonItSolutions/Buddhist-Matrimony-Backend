package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.exception.FileStorageException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;
import java.util.UUID;

/**
 * Validation and storage-key rules shared by every FileStorageService implementation.
 */
final class FileStorageSupport {
    private FileStorageSupport() {
    }

    static String photoKey(AppProperties properties, MultipartFile file, String matrimonyId) {
        checkSize(properties, file);
        String contentType = file.getContentType();
        if (contentType == null || !properties.fileStorage().allowedPhotoContentTypes().contains(contentType)) {
            throw new FileStorageException("Unsupported photo content type");
        }
        return "profiles/" + matrimonyId + "/" + UUID.randomUUID() + extension(file, "photo", contentType);
    }

    static String documentKey(AppProperties properties, MultipartFile file, String matrimonyId) {
        checkSize(properties, file);
        String contentType = file.getContentType();
        if (contentType == null || !(properties.fileStorage().allowedPhotoContentTypes().contains(contentType) || contentType.equals("application/pdf"))) {
            throw new FileStorageException("Unsupported document content type. Must be PDF or image.");
        }
        return "documents/" + matrimonyId + "/" + UUID.randomUUID() + extension(file, "document", contentType);
    }

    private static void checkSize(AppProperties properties, MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }
        if (file.getSize() > properties.fileStorage().maxPhotoSizeBytes()) {
            throw new FileStorageException("File size exceeds configured maximum");
        }
    }

    private static String extension(MultipartFile file, String fallbackName, String contentType) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename() == null ? fallbackName : file.getOriginalFilename());
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
