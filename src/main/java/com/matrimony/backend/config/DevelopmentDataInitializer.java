package com.matrimony.backend.config;

import com.matrimony.backend.entity.User;
import com.matrimony.backend.enums.AccountStatus;
import com.matrimony.backend.enums.Role;
import com.matrimony.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class DevelopmentDataInitializer implements CommandLineRunner {
    private final AppProperties properties;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String password = properties.development().adminPassword();
        if (!StringUtils.hasText(password)) {
            return;
        }
        if (userRepository.existsByEmailIgnoreCase(properties.development().adminEmail())) {
            return;
        }
        User admin = new User();
        admin.setMatrimonyId("MATADMIN");
        admin.setEmail(properties.development().adminEmail().toLowerCase());
        admin.setCountryCode("+91");
        admin.setMobileNumber(properties.development().adminMobile());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setRole(Role.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        admin.setEmailVerified(true);
        admin.setMobileVerified(true);
        admin.setTermsAcceptedAt(LocalDateTime.now());
        admin.setPrivacyPolicyAcceptedAt(LocalDateTime.now());
        admin.setTermsVersion("2026.08");
        admin.setPrivacyPolicyVersion("2026.08");
        userRepository.save(admin);
    }
}
