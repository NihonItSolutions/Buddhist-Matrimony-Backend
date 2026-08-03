package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.ConversationResponse;
import com.matrimony.backend.dto.response.InteractionResponses.MessageResponse;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Messaging")
public class MessagingController {
    private final InteractionService service;

    @GetMapping("/api/conversations")
    ApiResponse<PageResponse<ConversationResponse>> conversations(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Conversations", service.conversations(pageable));
    }

    @PostMapping("/api/conversations/{matrimonyId}")
    ApiResponse<ConversationResponse> create(@PathVariable String matrimonyId) {
        return ApiResponse.ok("Conversation ready", service.createConversation(matrimonyId));
    }

    @GetMapping("/api/conversations/{conversationId}/messages")
    ApiResponse<PageResponse<MessageResponse>> messages(@PathVariable Long conversationId, @PageableDefault(size = 50) Pageable pageable) {
        return ApiResponse.ok("Messages", service.messages(conversationId, pageable));
    }

    @PostMapping("/api/conversations/{conversationId}/messages")
    ApiResponse<MessageResponse> send(@PathVariable Long conversationId, @Valid @RequestBody InteractionRequests.MessageRequest request) {
        return ApiResponse.ok("Message sent", service.sendMessage(conversationId, request));
    }

    @PatchMapping("/api/conversations/{conversationId}/read")
    ApiResponse<Void> read(@PathVariable Long conversationId) {
        service.markConversationRead(conversationId);
        return ApiResponse.ok("Conversation marked read", null);
    }

    @DeleteMapping("/api/messages/{messageId}")
    ResponseEntity<Void> delete(@PathVariable Long messageId) {
        service.deleteMessage(messageId);
        return ResponseEntity.noContent().build();
    }
}
