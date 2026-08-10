package com.matrimony.backend.service.impl;

import com.matrimony.backend.service.OtpSender;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class ConsoleOtpSender implements OtpSender {
    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final String mailUsername;
    private final String mailPassword;

    public ConsoleOtpSender(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${spring.mail.username:}") String fromAddress,
            @Value("${spring.mail.username:}") String mailUsername,
            @Value("${spring.mail.password:}") String mailPassword
    ) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.mailUsername = mailUsername;
        this.mailPassword = mailPassword;
    }

    @Override
    public void send(String destination, String otp) {
        if (isEmail(destination) && sendEmail(destination, otp)) {
            System.out.printf("DEV OTP emailed to %s%n", destination);
            return;
        }
        System.out.printf("DEV OTP for %s: %s%n", destination, otp);
    }

    private boolean sendEmail(String destination, String otp) {
        if (isBlank(mailUsername) || isBlank(mailPassword)) {
            System.out.println("DEV OTP mail not configured. Set MAIL_USERNAME and MAIL_PASSWORD to send email OTP.");
            return false;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            return false;
        }

        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.isBlank()) {
            message.setFrom(fromAddress);
        }
        message.setTo(destination);
        message.setSubject("Your Buddhist Matrimony OTP");
        message.setText("""
                Your Buddhist Matrimony verification OTP is %s.

                This OTP is valid for a few minutes. Please do not share it with anyone.
                """.formatted(otp));

        try {
            mailSender.send(message);
            return true;
        } catch (MailAuthenticationException ex) {
            System.out.println("DEV OTP mail authentication failed. Use your Gmail address as MAIL_USERNAME and a Gmail App Password as MAIL_PASSWORD.");
            return false;
        } catch (MailException ex) {
            System.out.printf("DEV OTP mail send failed for %s: %s%n", destination, ex.getMessage());
            return false;
        }
    }

    private boolean isEmail(String destination) {
        return destination != null && destination.contains("@");
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
