package com.asuni.blogservice.Auth.service.impl;

import com.asuni.blogservice.Auth.dto.request.VerifyAccountRequest;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.Auth.service.contract.VerificationService;
import com.asuni.blogservice.exceptions.BadRequestException;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VerificationServiceImpl implements VerificationService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public void verifyAccount(VerifyAccountRequest request) {

        var claims = jwtUtil.validateAndExtract(request.getVerification_token());

        String type = claims.get("type", String.class);
        String email = claims.getSubject();
        String phone = claims.get("phone", String.class);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if ("EMAIL_VERIFICATION".equals(type)) {

            otpService.verifyEmailOtp(
                    email,
                    request.getEmail_otp(),
                    OtpType.EMAIL_VERIFICATION
            );

            user.setEmailVerified(true);
        }

        else if ("PHONE_VERIFICATION".equals(type)) {

            otpService.verifyPhoneOtp(
                    phone,
                    request.getPhone_otp(),
                    OtpType.PHONE_VERIFICATION
            );

            user.setPhoneNumberVerified(true);
        }

        else if ("BOTH_VERIFICATION".equals(type)) {

            otpService.verifyEmailOtp(
                    email,
                    request.getEmail_otp(),
                    OtpType.EMAIL_VERIFICATION
            );

            otpService.verifyPhoneOtp(
                    phone,
                    request.getPhone_otp(),
                    OtpType.PHONE_VERIFICATION
            );

            user.setEmailVerified(true);
            user.setPhoneNumberVerified(true);
        }

        else {
            throw new BadRequestException("Invalid token type");
        }

        userRepository.save(user);
    }
}
