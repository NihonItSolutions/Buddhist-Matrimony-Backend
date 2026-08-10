package com.matrimony.backend.service.impl;

import com.matrimony.backend.config.AppProperties;
import com.matrimony.backend.dto.request.*;
import com.matrimony.backend.dto.response.AuthResponse;
import com.matrimony.backend.dto.response.UserMeResponse;
import com.matrimony.backend.entity.*;
import com.matrimony.backend.enums.*;
import com.matrimony.backend.exception.*;
import com.matrimony.backend.repository.*;
import com.matrimony.backend.security.CurrentUser;
import com.matrimony.backend.security.JwtService;
import com.matrimony.backend.service.AuthService;
import com.matrimony.backend.service.OtpSender;
import com.matrimony.backend.util.HashingUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String TERMS_VERSION = "2026.08";
    private static final String PRIVACY_VERSION = "2026.08";

    private final UserRepository userRepository;
    private final MatrimonyProfileRepository profileRepository;
    private final ProfilePrivacySettingRepository privacySettingRepository;
    private final OtpTokenRepository otpTokenRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUser currentUser;
    private final AppProperties properties;
    private final OtpSender otpSender;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        String mobileNumber = request.mobileNumber().trim();
        String countryCode = request.countryCode().trim();
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("Email is already registered");
        }
        if (userRepository.existsByMobileNumber(mobileNumber)) {
            throw new DuplicateResourceException("Mobile number is already registered");
        }

        User user = new User();
        user.setMatrimonyId(nextMatrimonyId());
        user.setEmail(email);
        user.setMobileNumber(mobileNumber);
        user.setCountryCode(countryCode);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.PENDING_VERIFICATION);
        user.setTermsAcceptedAt(LocalDateTime.now());
        user.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
        user.setTermsVersion(TERMS_VERSION);
        user.setPrivacyPolicyVersion(PRIVACY_VERSION);
        userRepository.save(user);

        MatrimonyProfile profile = new MatrimonyProfile();
        profile.setUser(user);
        profile.setProfileCreatedFor(request.profileCreatedFor());
        profile.setFirstName(request.firstName());
        profile.setLastName(request.lastName());
        profile.setGender(request.gender());
        profile.setProfileStatus(ProfileStatus.DRAFT);
        profile.setLastActiveAt(LocalDateTime.now());
        profileRepository.save(profile);

        ProfilePrivacySetting privacy = new ProfilePrivacySetting();
        privacy.setProfile(profile);
        privacySettingRepository.save(privacy);

        sendOtp(new OtpRequest(user.getEmail()), OtpPurpose.EMAIL_VERIFICATION);
        return authResponse(user);
    }

    @Override
    @Transactional
    public void sendOtp(OtpRequest request, OtpPurpose purpose) {
        OtpToken previous = otpTokenRepository.findFirstByDestinationAndPurposeOrderByCreatedAtDesc(request.destination(), purpose).orElse(null);
        if (previous != null && previous.getNextResendAt().isAfter(LocalDateTime.now())) {
            throw new DailyLimitExceededException("OTP resend cooldown is active");
        }
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));
        OtpToken token = new OtpToken();
        token.setDestination(request.destination());
        token.setPurpose(purpose);
        token.setOtpHash(HashingUtil.sha256(otp));
        token.setExpiresAt(LocalDateTime.now().plus(properties.otp().expiration()));
        token.setNextResendAt(LocalDateTime.now().plus(properties.otp().resendCooldown()));
        token.setResendCount(previous == null ? 0 : previous.getResendCount() + 1);
        otpTokenRepository.save(token);
        otpSender.send(request.destination(), otp);
    }

    @Override
    @Transactional
    public void verifyOtp(OtpVerifyRequest request, OtpPurpose purpose) {
        OtpToken token = otpTokenRepository.findFirstByDestinationAndPurposeOrderByCreatedAtDesc(request.destination(), purpose)
                .orElseThrow(() -> new OtpVerificationException("OTP not found"));
        verifyTokenValue(token, request.otp());
        User user = purpose == OtpPurpose.EMAIL_VERIFICATION
                ? userRepository.findByEmailIgnoreCase(request.destination()).orElseThrow(() -> new ResourceNotFoundException("User not found"))
                : userRepository.findByMobileNumber(request.destination()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (purpose == OtpPurpose.EMAIL_VERIFICATION) {
            user.setEmailVerified(true);
        }
        if (purpose == OtpPurpose.MOBILE_VERIFICATION) {
            user.setMobileVerified(true);
        }
        if (user.isEmailVerified() || user.isMobileVerified()) {
            user.setAccountStatus(AccountStatus.ACTIVE);
        }
    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.identifier(), request.password()));
        User user = loadByIdentifier(request.identifier());
        if (user.getAccountStatus() == AccountStatus.SUSPENDED) {
            throw new AccountSuspendedException("Account is suspended");
        }
        user.setFailedLoginAttempts(0);
        user.setLastLoginAt(LocalDateTime.now());
        user.setRefreshTokenVersion(user.getRefreshTokenVersion() + 1);
        return authResponse(user);
    }

    @Override
    @Transactional
    public void requestLoginOtp(OtpRequest request) {
        loadByIdentifier(request.destination());
        sendOtp(request, OtpPurpose.LOGIN);
    }

    @Override
    @Transactional
    public AuthResponse verifyLoginOtp(OtpVerifyRequest request) {
        OtpToken token = otpTokenRepository.findFirstByDestinationAndPurposeOrderByCreatedAtDesc(request.destination(), OtpPurpose.LOGIN)
                .orElseThrow(() -> new OtpVerificationException("OTP not found"));
        verifyTokenValue(token, request.otp());
        User user = loadByIdentifier(request.destination());
        user.setLastLoginAt(LocalDateTime.now());
        user.setRefreshTokenVersion(user.getRefreshTokenVersion() + 1);
        return authResponse(user);
    }

    @Override
    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {
        Claims claims = jwtService.parse(request.refreshToken());
        if (!jwtService.isTokenType(claims, "refresh")) {
            throw new UnauthorizedOperationException("Invalid refresh token");
        }
        User user = userRepository.findByEmailIgnoreCase(claims.getSubject())
                .orElseThrow(() -> new UnauthorizedOperationException("Invalid refresh token"));
        Number tokenVersion = claims.get("version", Number.class);
        if (tokenVersion == null || tokenVersion.longValue() != user.getRefreshTokenVersion()) {
            throw new UnauthorizedOperationException("Refresh token has been rotated");
        }
        user.setRefreshTokenVersion(user.getRefreshTokenVersion() + 1);
        return authResponse(user);
    }

    @Override
    @Transactional
    public void logout() {
        User user = currentUser.get();
        user.setRefreshTokenVersion(user.getRefreshTokenVersion() + 1);
    }

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        User user = loadByIdentifier(request.identifier());
        String token = UUID.randomUUID().toString() + UUID.randomUUID();
        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(HashingUtil.sha256(token));
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        passwordResetTokenRepository.save(resetToken);
        System.out.printf("DEV password reset token for %s: %s%n", request.identifier(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        PasswordResetToken token = passwordResetTokenRepository.findByTokenHash(HashingUtil.sha256(request.token()))
                .orElseThrow(() -> new InvalidRequestException("Invalid reset token"));
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidRequestException("Reset token expired");
        }
        token.getUser().setPasswordHash(passwordEncoder.encode(request.newPassword()));
        token.getUser().setRefreshTokenVersion(token.getUser().getRefreshTokenVersion() + 1);
        token.setUsed(true);
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = currentUser.get();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash())) {
            throw new UnauthorizedOperationException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setRefreshTokenVersion(user.getRefreshTokenVersion() + 1);
    }

    @Override
    @Transactional(readOnly = true)
    public UserMeResponse me() {
        User user = currentUser.get();
        int completion = profileRepository.findByUser(user).map(MatrimonyProfile::getProfileCompleteness).orElse(0);
        return new UserMeResponse(
                user.getId(),
                user.getMatrimonyId(),
                user.getEmail(),
                user.getCountryCode() + user.getMobileNumber(),
                user.getRole(),
                user.getAccountStatus(),
                user.isEmailVerified(),
                user.isMobileVerified(),
                completion
        );
    }

    private User loadByIdentifier(String identifier) {
        String normalizedIdentifier = identifier == null ? "" : identifier.trim();
        return userRepository.findByEmailIgnoreCase(normalizedIdentifier)
                .or(() -> userRepository.findByMobileNumber(normalizedIdentifier))
                .orElseThrow(() -> new UnauthorizedOperationException("Invalid credentials"));
    }

    private void verifyTokenValue(OtpToken token, String submittedOtp) {
        if (token.isVerified()) {
            throw new OtpVerificationException("OTP already used");
        }
        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP expired");
        }
        if (token.getAttempts() >= properties.otp().maxAttempts()) {
            throw new OtpVerificationException("OTP maximum attempts exceeded");
        }
        token.setAttempts(token.getAttempts() + 1);
        if (!token.getOtpHash().equals(HashingUtil.sha256(submittedOtp))) {
            throw new OtpVerificationException("Invalid OTP");
        }
        token.setVerified(true);
    }

    private AuthResponse authResponse(User user) {
        int completion = profileRepository.findByUser(user).map(MatrimonyProfile::getProfileCompleteness).orElse(0);
        return new AuthResponse(
                jwtService.generateAccessToken(user),
                jwtService.generateRefreshToken(user),
                "Bearer",
                user.getMatrimonyId(),
                user.getRole(),
                user.getAccountStatus(),
                completion
        );
    }

    private String nextMatrimonyId() {
        long next = Math.max(100001L, userRepository.maxId() + 100001L);
        return "MAT" + next;
    }
}
