package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.VerifyAccountRequest;

public interface VerificationService {

    /**
     * Verifies email and phone OTP using verificationToken
     */
    void verifyAccount(VerifyAccountRequest request);
}
