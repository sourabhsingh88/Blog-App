package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.VerifyAccountRequest;


public interface VerificationService {

    /**
     * Verifies email and phone OTP using verification_token
     */
    void verifyAccount(VerifyAccountRequest request);
}
