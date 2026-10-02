package com.matrimony.backend.controller;

import com.matrimony.backend.service.impl.SupabaseStorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * Keeps old /files/** links working when uploads live in Supabase Storage, by redirecting to the stored file.
 */
@RestController
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.file-storage.provider", havingValue = "supabase")
public class SupabaseFilesController {
    private final SupabaseStorageService storage;

    @GetMapping("/files/**")
    ResponseEntity<Void> file(HttpServletRequest request) {
        String key = request.getRequestURI().substring(request.getContextPath().length() + "/files/".length());
        if (key.isBlank() || key.contains("..")) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(storage.resolve(key))).build();
    }
}
