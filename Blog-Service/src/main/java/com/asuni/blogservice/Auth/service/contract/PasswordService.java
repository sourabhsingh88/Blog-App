package com.asuni.blogservice.Auth.service.contract;


import com.asuni.blogservice.Auth.dto.request.ChangePasswordRequest;
import com.asuni.blogservice.Auth.dto.request.ResetPasswordOtpRequest;
import com.asuni.blogservice.Auth.dto.request.ResetPasswordRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyResetOtpRequest;
import com.asuni.blogservice.Auth.entity.User;

public interface PasswordService {

    String forgotPasswordOtp(ResetPasswordOtpRequest request);

    String verifyResetOtp(VerifyResetOtpRequest request);

    void resetPassword(ResetPasswordRequest request);

    void changePassword(Long userId, ChangePasswordRequest request);
}
