package com.matrimony.backend.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.util.List;

@Validated
@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Jwt jwt,
        Otp otp,
        Profile profile,
        FileStorage fileStorage,
        Cors cors,
        Payment payment,
        Development development
) {
    public record Jwt(
            @NotBlank String secret,
            Duration accessTokenExpiration,
            Duration refreshTokenExpiration
    ) {
    }

    public record Otp(
            Duration expiration,
            Duration resendCooldown,
            @Min(1) int maxAttempts
    ) {
    }

    public record Profile(
            @Min(18) int minimumAge,
            Duration profileViewDeduplicationPeriod,
            @Min(1) int maxPhotos
    ) {
    }

    public record FileStorage(
            @NotBlank String location,
            long maxPhotoSizeBytes,
            @NotEmpty List<String> allowedPhotoContentTypes,
            String provider,
            String supabaseUrl,
            String supabaseServiceKey,
            String publicBucket,
            String privateBucket
    ) {
    }

    public record Cors(
            @NotEmpty List<String> allowedOrigins
    ) {
    }

    public record Payment(
            @NotBlank String gateway,
            @NotBlank String webhookSecret,
            @NotBlank String signatureSecret,
            Stripe stripe,
            Cashfree cashfree,
            Upi upi
    ) {
        public record Stripe(
                String secretKey,
                String publishableKey
        ) {}

        public record Cashfree(
                String appId,
                String secretKey,
                String env,
                String apiVersion
        ) {}

        public record Upi(
                String vpa,
                String payeeName,
                String adminAlertEmail
        ) {}
    }

    public record Development(
            String adminEmail,
            String adminMobile,
            String adminPassword
    ) {
    }
}
