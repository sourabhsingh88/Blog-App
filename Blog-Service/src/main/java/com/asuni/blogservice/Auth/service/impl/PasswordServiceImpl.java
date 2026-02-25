package com.asuni.blogservice.Auth.service.impl;

import com.asuni.blogservice.Auth.dto.request.ChangePasswordRequest;
import com.asuni.blogservice.Auth.dto.request.ResetPasswordOtpRequest;
import com.asuni.blogservice.Auth.dto.request.ResetPasswordRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyResetOtpRequest;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.Auth.service.contract.PasswordService;
import com.asuni.blogservice.exceptions.BadRequestException;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.security.JwtUtil;
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
    public void changePassword(Long userId, ChangePasswordRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getOld_password(), user.getPassword())) {
            throw new BadRequestException("Old password incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNew_password()));
        userRepository.save(user);
    }

}
