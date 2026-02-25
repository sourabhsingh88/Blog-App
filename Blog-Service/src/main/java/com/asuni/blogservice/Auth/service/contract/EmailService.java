package com.asuni.blogservice.Auth.service.contract;

public interface EmailService {

    void sendOtp(String to, String otp);
}
