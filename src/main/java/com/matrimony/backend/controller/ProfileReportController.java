package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.InteractionRequests;
import com.matrimony.backend.dto.response.InteractionResponses.ReportResponse;
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
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "Reports")
public class ProfileReportController {
    private final InteractionService service;

    @PostMapping("/profiles/{matrimonyId}")
    ResponseEntity<ApiResponse<ReportResponse>> report(@PathVariable String matrimonyId, @Valid @RequestBody InteractionRequests.ReportRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Report submitted", service.report(matrimonyId, request)));
    }

    @GetMapping("/me")
    ApiResponse<PageResponse<ReportResponse>> me(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("My reports", service.myReports(pageable));
    }
}
