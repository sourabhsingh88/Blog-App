package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.exceptions.NotFoundException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.service.contract.PasswordService;
import com.sourabh.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Override
    public String forgotPasswordOtp(ResetPasswordOtpRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("User not found"));

        otpService.generateEmailOtp(
                user.getEmail(),
                OtpType.FORGOT_PASSWORD
        );

        return jwtUtil.generateOtpToken(
                user.getEmail(),
                null,
                "RESET_OTP"
        );
    }

    @Override
    public String verifyResetOtp(VerifyResetOtpRequest request) {

        var claims = jwtUtil.validateAndExtract(request.getReset_otp_token());

        if (!claims.get("type").equals("RESET_OTP")) {
            throw new BadRequestException("Invalid token type");
        }

        String email = claims.getSubject();

        otpService.verifyEmailOtp(
                email,
                request.getOtp(),
                OtpType.FORGOT_PASSWORD
        );

        return jwtUtil.generateOtpToken(
                email,
                null,
                "RESET_PASSWORD"
        );
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getNew_password().equals(request.getConfirm_password())) {
            throw new BadRequestException("Passwords do not match");
        }

        var claims = jwtUtil.validateAndExtract(request.getReset_password_token());

        if (!claims.get("type").equals("RESET_PASSWORD")) {
            throw new BadRequestException("Invalid token type");
        }

        String email = claims.getSubject();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(user);
    }
    @Override
    public void changePassword(User user, ChangePasswordRequest request) {

        if (!passwordEncoder.matches(request.getOld_password(), user.getPassword())) {
            throw new BadRequestException("Old password incorrect");
        }

        if (!request.getNew_password().equals(request.getConfirm_password())) {
            throw new BadRequestException("Passwords do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(user);
    }

}
