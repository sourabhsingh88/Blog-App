package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.SignupRequest;

public interface RegistrationService {

    /**
     * Registers user and returns verification token
     */
    String signup(SignupRequest request);
}
