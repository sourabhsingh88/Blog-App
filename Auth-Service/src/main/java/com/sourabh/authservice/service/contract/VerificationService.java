package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.VerifyAccountRequest;

public interface VerificationService {

    /**
     * Verifies email and phone OTP using verification_token
     */
    void verifyAccount(VerifyAccountRequest request);
}
