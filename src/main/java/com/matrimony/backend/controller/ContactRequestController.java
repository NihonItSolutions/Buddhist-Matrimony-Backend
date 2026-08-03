package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.response.InteractionResponses.ContactDetailsResponse;
import com.matrimony.backend.dto.response.InteractionResponses.ContactRequestResponse;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact-requests")
@RequiredArgsConstructor
@Tag(name = "Contact Requests")
public class ContactRequestController {
    private final InteractionService service;

    @PostMapping("/{matrimonyId}")
    ApiResponse<ContactRequestResponse> request(@PathVariable String matrimonyId) {
        return ApiResponse.ok("Contact request sent", service.requestContact(matrimonyId));
    }

    @GetMapping("/sent")
    ApiResponse<PageResponse<ContactRequestResponse>> sent(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Sent contact requests", service.sentContactRequests(pageable));
    }

    @GetMapping("/received")
    ApiResponse<PageResponse<ContactRequestResponse>> received(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Received contact requests", service.receivedContactRequests(pageable));
    }

    @PatchMapping("/{requestId}/approve")
    ApiResponse<ContactRequestResponse> approve(@PathVariable Long requestId) {
        return ApiResponse.ok("Contact request approved", service.approveContact(requestId));
    }

    @PatchMapping("/{requestId}/reject")
    ApiResponse<ContactRequestResponse> reject(@PathVariable Long requestId) {
        return ApiResponse.ok("Contact request rejected", service.rejectContact(requestId));
    }

    @PatchMapping("/{requestId}/cancel")
    ApiResponse<ContactRequestResponse> cancel(@PathVariable Long requestId) {
        return ApiResponse.ok("Contact request cancelled", service.cancelContact(requestId));
    }

    @GetMapping("/{matrimonyId}/contact-details")
    ApiResponse<ContactDetailsResponse> details(@PathVariable String matrimonyId) {
        return ApiResponse.ok("Contact details", service.contactDetails(matrimonyId));
    }
}
