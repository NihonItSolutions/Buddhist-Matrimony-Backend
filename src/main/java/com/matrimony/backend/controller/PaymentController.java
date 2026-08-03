package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.PageResponse;
import com.matrimony.backend.dto.request.PaymentRequests;
import com.matrimony.backend.dto.response.PaymentResponses.OrderResponse;
import com.matrimony.backend.dto.response.PaymentResponses.PaymentResponse;
import com.matrimony.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments")
public class PaymentController {
    private final PaymentService service;

    @PostMapping("/orders")
    ApiResponse<OrderResponse> order(@Valid @RequestBody PaymentRequests.CreateOrderRequest request) {
        return ApiResponse.ok("Payment order created", service.createOrder(request));
    }

    @PostMapping("/verify")
    ApiResponse<PaymentResponse> verify(@Valid @RequestBody PaymentRequests.VerifyPaymentRequest request) {
        return ApiResponse.ok("Payment verified", service.verify(request));
    }

    @PostMapping("/webhook")
    ApiResponse<Void> webhook(@RequestHeader(value = "X-Payment-Signature", required = false) String signature,
                              @Valid @RequestBody PaymentRequests.WebhookRequest request) {
        service.webhook(request, signature);
        return ApiResponse.ok("Webhook processed", null);
    }

    @GetMapping("/me")
    ApiResponse<PageResponse<PaymentResponse>> me(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.ok("Payments", service.myPayments(pageable));
    }

    @GetMapping("/{paymentId}")
    ApiResponse<PaymentResponse> one(@PathVariable Long paymentId) {
        return ApiResponse.ok("Payment", service.myPayment(paymentId));
    }
}
