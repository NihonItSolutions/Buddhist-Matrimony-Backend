package com.matrimony.backend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    StoredFile storeProfilePhoto(MultipartFile file, String matrimonyId);

    StoredFile storeDocument(MultipartFile file, String matrimonyId);

    void delete(String storageKey);

    record StoredFile(String storageKey, String url, String thumbnailUrl) {
    }
}
