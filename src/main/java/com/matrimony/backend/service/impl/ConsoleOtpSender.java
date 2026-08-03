package com.matrimony.backend.service.impl;

import com.matrimony.backend.service.OtpSender;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("dev")
public class ConsoleOtpSender implements OtpSender {
    @Override
    public void send(String destination, String otp) {
        System.out.printf("DEV OTP for %s: %s%n", destination, otp);
    }
}
