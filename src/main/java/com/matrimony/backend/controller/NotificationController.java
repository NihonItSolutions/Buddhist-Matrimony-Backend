package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.response.InteractionResponses.NotificationResponse;
import com.matrimony.backend.enums.NotificationType;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications")
public class NotificationController {
    private final InteractionService service;

    @GetMapping
    ApiResponse<PageResponse<NotificationResponse>> list(@RequestParam(required = false) NotificationType type, @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Notifications", service.notifications(type, pageable));
    }

    @GetMapping("/unread-count")
    ApiResponse<Map<String, Long>> unreadCount() {
        return ApiResponse.ok("Unread count", Map.of("count", service.unreadNotificationCount()));
    }

    @PatchMapping("/{notificationId}/read")
    ApiResponse<Void> read(@PathVariable Long notificationId) {
        service.readNotification(notificationId);
        return ApiResponse.ok("Notification read", null);
    }

    @PatchMapping("/read-all")
    ApiResponse<Void> readAll() {
        service.readAllNotifications();
        return ApiResponse.ok("Notifications read", null);
    }

    @DeleteMapping("/{notificationId}")
    ResponseEntity<Void> delete(@PathVariable Long notificationId) {
        service.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
