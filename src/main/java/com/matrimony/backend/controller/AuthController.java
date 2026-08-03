package com.matrimony.backend.controller;

import com.matrimony.backend.dto.ApiResponse;
import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.AuthResponse;
import com.matrimony.backend.dto.response.UserMeResponse;
import com.matrimony.backend.enums.OtpPurpose;
import com.matrimony.backend.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("Registration successful", authService.register(request)));
    }

    @PostMapping("/mobile-otp/send")
    ApiResponse<Void> sendMobileOtp(@Valid @RequestBody OtpRequest request) {
        authService.sendOtp(request, OtpPurpose.MOBILE_VERIFICATION);
        return ApiResponse.ok("OTP sent", null);
    }

    @PostMapping("/mobile-otp/verify")
    ApiResponse<Void> verifyMobileOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request, OtpPurpose.MOBILE_VERIFICATION);
        return ApiResponse.ok("Mobile verified", null);
    }

    @PostMapping("/mobile-otp/resend")
    ApiResponse<Void> resendMobileOtp(@Valid @RequestBody OtpRequest request) {
        authService.sendOtp(request, OtpPurpose.MOBILE_VERIFICATION);
        return ApiResponse.ok("OTP resent", null);
    }

    @PostMapping("/email-verification/send")
    ApiResponse<Void> sendEmailOtp(@Valid @RequestBody OtpRequest request) {
        authService.sendOtp(request, OtpPurpose.EMAIL_VERIFICATION);
        return ApiResponse.ok("Email verification sent", null);
    }

    @PostMapping("/email-verification/verify")
    ApiResponse<Void> verifyEmailOtp(@Valid @RequestBody OtpVerifyRequest request) {
        authService.verifyOtp(request, OtpPurpose.EMAIL_VERIFICATION);
        return ApiResponse.ok("Email verified", null);
    }

    @PostMapping("/login")
    ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok("Login successful", authService.login(request));
    }

    @PostMapping("/login/otp/request")
    ApiResponse<Void> requestLoginOtp(@Valid @RequestBody OtpRequest request) {
        authService.requestLoginOtp(request);
        return ApiResponse.ok("Login OTP sent", null);
    }

    @PostMapping("/login/otp/verify")
    ApiResponse<AuthResponse> verifyLoginOtp(@Valid @RequestBody OtpVerifyRequest request) {
        return ApiResponse.ok("Login successful", authService.verifyLoginOtp(request));
    }

    @PostMapping("/token/refresh")
    ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return ApiResponse.ok("Token refreshed", authService.refresh(request));
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.ok("Logged out", null);
    }

    @PostMapping("/forgot-password")
    ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ApiResponse.ok("Password reset instructions sent", null);
    }

    @PostMapping("/reset-password")
    ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ApiResponse.ok("Password reset successful", null);
    }

    @PostMapping("/change-password")
    ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        authService.changePassword(request);
        return ApiResponse.ok("Password changed", null);
    }

    @GetMapping("/me")
    ApiResponse<UserMeResponse> me() {
        return ApiResponse.ok("Current user", authService.me());
    }
}
