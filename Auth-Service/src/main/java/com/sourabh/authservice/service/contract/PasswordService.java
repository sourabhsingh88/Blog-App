package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.entity.User;

public interface PasswordService {

    /**
     * Sends OTP and returns resetOtpToken
     */
    String forgotPasswordOtp(ResetPasswordOtpRequest request);

    /**
     * Verifies OTP and returns resetPasswordToken
     */
    String verifyResetOtp(VerifyResetOtpRequest request);

    /**
     * Resets password using resetPasswordToken
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * Change password for authenticated user
     */
    void changePassword(User user, ChangePasswordRequest request);

}
