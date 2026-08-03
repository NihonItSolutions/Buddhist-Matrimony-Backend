package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.InterestResponse;
import com.matrimony.backend.service.InteractionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/interests")
@RequiredArgsConstructor
@Tag(name = "Interests")
public class InterestController {
    private final InteractionService service;

    @PostMapping("/{receiverMatrimonyId}")
    ResponseEntity<ApiResponse<InterestResponse>> send(@PathVariable String receiverMatrimonyId, @Valid @RequestBody(required = false) InteractionRequests.InterestMessage request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Interest sent", service.sendInterest(receiverMatrimonyId, request)));
    }

    @GetMapping("/sent")
    ApiResponse<PageResponse<InterestResponse>> sent(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Sent interests", service.sentInterests(pageable));
    }

    @GetMapping("/received")
    ApiResponse<PageResponse<InterestResponse>> received(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Received interests", service.receivedInterests(pageable));
    }

    @GetMapping({"/accepted", "/declined"})
    ApiResponse<PageResponse<InterestResponse>> byStatus(jakarta.servlet.http.HttpServletRequest request, @PageableDefault(size = 20) Pageable pageable) {
        String status = request.getRequestURI().endsWith("accepted") ? "ACCEPTED" : "DECLINED";
        return ApiResponse.ok("Interests", service.interestsByStatus(status, pageable));
    }

    @PatchMapping("/{interestId}/accept")
    ApiResponse<InterestResponse> accept(@PathVariable Long interestId) {
        return ApiResponse.ok("Interest accepted", service.acceptInterest(interestId));
    }

    @PatchMapping("/{interestId}/decline")
    ApiResponse<InterestResponse> decline(@PathVariable Long interestId) {
        return ApiResponse.ok("Interest declined", service.declineInterest(interestId));
    }

    @PatchMapping("/{interestId}/cancel")
    ApiResponse<InterestResponse> cancel(@PathVariable Long interestId) {
        return ApiResponse.ok("Interest cancelled", service.cancelInterest(interestId));
    }

    @DeleteMapping("/{interestId}")
    ResponseEntity<Void> delete(@PathVariable Long interestId) {
        service.deleteInterest(interestId);
        return ResponseEntity.noContent().build();
    }
}
