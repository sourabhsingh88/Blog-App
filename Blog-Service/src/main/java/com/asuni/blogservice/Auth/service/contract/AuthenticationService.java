package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.LoginPhoneRequest;
import com.asuni.blogservice.Auth.dto.request.LoginRequest;
import com.asuni.blogservice.Auth.dto.request.RefreshTokenRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyPhoneOtpRequest;
import com.asuni.blogservice.Auth.dto.response.LoginResponse;
import com.asuni.blogservice.Auth.dto.response.PhoneLoginResponse;

public interface AuthenticationService {

    LoginResponse login(LoginRequest request);

    PhoneLoginResponse sendPhoneLoginOtp(LoginPhoneRequest request);


    LoginResponse  verifyPhoneLoginOtp(VerifyPhoneOtpRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

     Long getUserIdByUsername(String username);
}
