package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.LoginPhoneRequest;
import com.asuni.blogservice.Auth.dto.request.LoginRequest;
import com.asuni.blogservice.Auth.dto.request.RefreshTokenRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyPhoneOtpRequest;
import com.asuni.blogservice.Auth.dto.response.LoginResponse;
import com.asuni.blogservice.Auth.dto.response.PhoneLoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    /**
     * Generates OTP and returns phone_login_token
     */
    PhoneLoginResponse sendPhoneLoginOtp(LoginPhoneRequest request);

    /**
     * Verifies OTP and returns AUTH token
     */
    LoginResponse  verifyPhoneLoginOtp(VerifyPhoneOtpRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

     Long getUserIdByUsername(String username);
}
