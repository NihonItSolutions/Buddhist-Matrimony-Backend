package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.SupportRequests;
import com.matrimony.backend.service.SupportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Support and Success Stories")
public class SupportController {
    private final SupportService service;

    @PostMapping("/api/support/tickets")
    ApiResponse<Object> createTicket(@Valid @RequestBody SupportRequests.TicketRequest request) {
        return ApiResponse.ok("Support ticket created", service.createTicket(request));
    }

    @GetMapping("/api/support/tickets/me")
    ApiResponse<PageResponse<?>> tickets(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Support tickets", service.myTickets(pageable));
    }

    @GetMapping("/api/support/tickets/{ticketId}")
    ApiResponse<Object> ticket(@PathVariable Long ticketId) {
        return ApiResponse.ok("Support ticket", service.ticket(ticketId));
    }

    @PostMapping("/api/support/tickets/{ticketId}/messages")
    ApiResponse<Object> message(@PathVariable Long ticketId, @Valid @RequestBody SupportRequests.TicketMessageRequest request) {
        return ApiResponse.ok("Ticket message added", service.addMessage(ticketId, request));
    }

    @PatchMapping("/api/support/tickets/{ticketId}/close")
    ApiResponse<Void> close(@PathVariable Long ticketId) {
        service.closeTicket(ticketId);
        return ApiResponse.ok("Ticket closed", null);
    }

    @PostMapping("/api/feedback")
    ApiResponse<Void> feedback(@Valid @RequestBody SupportRequests.FeedbackRequest request) {
        service.feedback(request);
        return ApiResponse.ok("Feedback submitted", null);
    }

    @PostMapping("/api/contact-us")
    ApiResponse<Void> contact(@Valid @RequestBody SupportRequests.ContactUsRequest request) {
        service.contactUs(request);
        return ApiResponse.ok("Contact request submitted", null);
    }

    @GetMapping("/api/success-stories")
    ApiResponse<PageResponse<?>> publicStories(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Success stories", service.publicStories(pageable));
    }

    @GetMapping("/api/success-stories/{storyId}")
    ApiResponse<Object> story(@PathVariable Long storyId) {
        return ApiResponse.ok("Success story", service.story(storyId));
    }

    @PostMapping("/api/success-stories")
    ApiResponse<Object> createStory(@Valid @RequestBody SupportRequests.SuccessStoryRequest request) {
        return ApiResponse.ok("Success story submitted", service.createSuccessStory(request));
    }

    @GetMapping("/api/success-stories/me")
    ApiResponse<PageResponse<?>> myStories(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("My success stories", service.myStories(pageable));
    }

    @PutMapping("/api/success-stories/{storyId}")
    ApiResponse<Object> updateStory(@PathVariable Long storyId, @Valid @RequestBody SupportRequests.SuccessStoryRequest request) {
        return ApiResponse.ok("Success story updated", service.updateStory(storyId, request));
    }

    @DeleteMapping("/api/success-stories/{storyId}")
    ResponseEntity<Void> deleteStory(@PathVariable Long storyId) {
        service.deleteStory(storyId);
        return ResponseEntity.noContent().build();
    }
}
