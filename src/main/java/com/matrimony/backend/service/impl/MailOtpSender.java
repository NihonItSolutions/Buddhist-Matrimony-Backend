package com.matrimony.backend.service.impl;

import com.matrimony.backend.service.OtpSender;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("!dev")
@RequiredArgsConstructor
public class MailOtpSender implements OtpSender {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void send(String destination, String otp) {
        if (!isEmail(destination)) {
            return;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(destination);
        message.setSubject("Your Buddhist Matrimony OTP");
        message.setText("""
                Your Buddhist Matrimony verification OTP is %s.

                This OTP is valid for a few minutes. Please do not share it with anyone.
                """.formatted(otp));
        mailSender.send(message);
    }

    private boolean isEmail(String destination) {
        return destination != null && destination.contains("@");
    }
}
