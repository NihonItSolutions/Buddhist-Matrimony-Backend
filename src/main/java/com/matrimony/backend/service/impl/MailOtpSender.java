package com.matrimony.backend.service.impl;

import com.matrimony.backend.service.OtpSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
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

        try {
            MimeMessage mime = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            helper.setFrom(fromEmail, "Buddhist Matrimony");
            helper.setTo(destination);
            helper.setSubject("\uD83D\uDD10 Your OTP for Buddhist Matrimony Verification");
            helper.setText(buildHtml(otp), true);
            mailSender.send(mime);
        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("Failed to send OTP email to {}", destination, e);
        }
    }

    private String buildHtml(String otp) {
        return "<!DOCTYPE html>" +
            "<html lang=\"en\"><head><meta charset=\"UTF-8\"/>" +
            "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1.0\"/>" +
            "<title>Your OTP - Buddhist Matrimony</title></head>" +
            "<body style=\"margin:0;padding:0;background:#f1f5f9;font-family:'Segoe UI',Arial,sans-serif;\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f1f5f9;padding:40px 16px;\">" +
            "<tr><td align=\"center\">" +
            "<table width=\"100%\" style=\"max-width:560px;background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 8px 30px rgba(0,0,0,0.10);\">" +

            "<!-- Header -->" +
            "<tr><td style=\"background:linear-gradient(135deg,#0f2b6f,#1e4bd8);padding:32px 36px 28px;text-align:center;\">" +
            "<span style=\"font-size:26px;display:block;margin-bottom:4px;\">☸</span>" +
            "<span style=\"font-size:22px;font-weight:900;color:#ffffff;letter-spacing:-0.5px;\">Buddhist Matrimony</span>" +
            "<p style=\"color:rgba(255,255,255,0.72);margin:8px 0 0;font-size:13px;\">Rooted in Dhamma &amp; Dignity</p>" +
            "</td></tr>" +

            "<!-- Panchsheel strip -->" +
            "<tr><td style=\"padding:0;\">" +
            "<table width=\"100%\" cellpadding=\"0\" cellspacing=\"0\"><tr>" +
            "<td style=\"background:#0057b8;height:4px;\"></td>" +
            "<td style=\"background:#ffd700;height:4px;\"></td>" +
            "<td style=\"background:#d62828;height:4px;\"></td>" +
            "<td style=\"background:#f5f5f5;height:4px;\"></td>" +
            "<td style=\"background:#f28c28;height:4px;\"></td>" +
            "</tr></table></td></tr>" +

            "<!-- Body -->" +
            "<tr><td style=\"padding:36px 36px 28px;\">" +
            "<h1 style=\"margin:0 0 8px;font-size:22px;font-weight:800;color:#0f172a;\">Verify Your Account</h1>" +
            "<p style=\"margin:0 0 28px;font-size:15px;color:#475569;line-height:1.7;\">" +
            "Thank you for registering with <strong>Buddhist Matrimony</strong>.<br/>" +
            "Please use the OTP below to complete your verification. This code expires in <strong>10 minutes</strong>." +
            "</p>" +

            "<!-- OTP Box -->" +
            "<div style=\"background:linear-gradient(135deg,#eef2ff,#e0e7ff);border:2px dashed #818cf8;border-radius:14px;padding:30px 20px;text-align:center;margin-bottom:28px;\">" +
            "<p style=\"margin:0 0 8px;font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:1.5px;color:#6366f1;\">Your One-Time Password</p>" +
            "<span style=\"font-size:52px;font-weight:900;letter-spacing:16px;color:#1e3a8a;display:block;line-height:1.1;\">" + otp + "</span>" +
            "<p style=\"margin:12px 0 0;font-size:12px;color:#64748b;\">&#9200; Valid for 10 minutes only</p>" +
            "</div>" +

            "<!-- Warning -->" +
            "<div style=\"background:#fff7ed;border-left:4px solid #f97316;border-radius:8px;padding:14px 18px;margin-bottom:24px;\">" +
            "<p style=\"margin:0;font-size:13px;color:#9a3412;line-height:1.6;\">" +
            "<strong>&#9888; Security Notice:</strong> Never share this OTP with anyone — including our support team. " +
            "Buddhist Matrimony will <em>never</em> ask for your OTP over call, chat, or email." +
            "</p></div>" +

            "<p style=\"margin:0;font-size:13px;color:#94a3b8;line-height:1.6;\">" +
            "If you did not request this, please ignore this email. Your account remains safe." +
            "</p></td></tr>" +

            "<!-- Footer -->" +
            "<tr><td style=\"background:#f8fafc;border-top:1px solid #e2e8f0;padding:22px 36px;text-align:center;\">" +
            "<p style=\"margin:0 0 4px;font-size:12px;color:#94a3b8;\">" +
            "&#169; 2025 Buddhist Matrimony &middot; Respectful matchmaking for Buddhist families" +
            "</p>" +
            "<p style=\"margin:0;font-size:11px;color:#cbd5e1;\">Zero Dowry &middot; Privacy First &middot; Dhamma Values</p>" +
            "</td></tr>" +

            "</table></td></tr></table></body></html>";
    }

    private boolean isEmail(String destination) {
        return destination != null && destination.contains("@");
    }
}
