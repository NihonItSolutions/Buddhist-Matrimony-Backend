package com.matrimony.backend.service;

public interface OtpSender {
    void send(String destination, String otp);
}
