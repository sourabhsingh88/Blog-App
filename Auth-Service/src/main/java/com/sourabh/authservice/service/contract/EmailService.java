package com.sourabh.authservice.service.contract;

public interface EmailService {

    void sendOtp(String to, String otp);
}
