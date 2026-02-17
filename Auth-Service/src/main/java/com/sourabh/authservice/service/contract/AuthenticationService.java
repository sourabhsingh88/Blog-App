package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.dto.response.LoginResponse;

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
