package com.matrimony.backend.service;

import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.AuthResponse;
import com.matrimony.backend.dto.response.UserMeResponse;
import com.matrimony.backend.enums.OtpPurpose;

public interface AuthService {
    AuthResponse register(RegisterRequest request);

    void sendOtp(OtpRequest request, OtpPurpose purpose);

    void verifyOtp(OtpVerifyRequest request, OtpPurpose purpose);

    AuthResponse login(LoginRequest request);

    void requestLoginOtp(OtpRequest request);

    AuthResponse verifyLoginOtp(OtpVerifyRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout();

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(ChangePasswordRequest request);

    UserMeResponse me();
}
