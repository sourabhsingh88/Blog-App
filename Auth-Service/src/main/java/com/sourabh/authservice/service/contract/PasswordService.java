package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.entity.User;

public interface PasswordService {

    /**
     * Sends OTP and returns reset_otp_token
     */
    String forgotPasswordOtp(ResetPasswordOtpRequest request);

    /**
     * Verifies OTP and returns reset_password_token
     */
    String verifyResetOtp(VerifyResetOtpRequest request);

    /**
     * Resets password using reset_password_token
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Change password for authenticated user
     */
    void changePassword(User user, ChangePasswordRequest request);

}
