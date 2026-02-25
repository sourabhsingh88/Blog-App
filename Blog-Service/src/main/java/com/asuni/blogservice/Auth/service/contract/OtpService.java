package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.enums.OtpType;

public interface OtpService {

    void generateEmailOtp(String email, OtpType type);

    void generatePhoneOtp(String phone, OtpType type);

    void verifyEmailOtp(String email, String otp, OtpType type);

    void verifyPhoneOtp(String phone, String otp, OtpType type);
}
