package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.VerifyAccountRequest;


public interface VerificationService {

    void verifyAccount(VerifyAccountRequest request);
}
