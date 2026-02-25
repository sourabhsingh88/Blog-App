package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.LoginPhoneRequest;
import com.asuni.blogservice.Auth.dto.request.LoginRequest;
import com.asuni.blogservice.Auth.dto.request.RefreshTokenRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyPhoneOtpRequest;
import com.asuni.blogservice.Auth.dto.response.LoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    /**
     * Generates OTP and returns phone_login_token
     */
    String sendPhoneLoginOtp(LoginPhoneRequest request);

    /**
     * Verifies OTP and returns AUTH token
     */
    String verifyPhoneLoginOtp(VerifyPhoneOtpRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

     Long getUserIdByUsername(String username);
}
