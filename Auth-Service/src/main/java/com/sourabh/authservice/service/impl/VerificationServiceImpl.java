package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.dto.request.VerifyAccountRequest;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.exceptions.NotFoundException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.service.contract.VerificationService;
import com.sourabh.authservice.util.JwtUtil;
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

        var claims = jwtUtil.validateAndExtract(request.getVerificationToken());

        String type = claims.get("type", String.class);
        String email = claims.getSubject();
        String phone = claims.get("phone", String.class);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if ("EMAIL_VERIFICATION".equals(type)) {

            otpService.verifyEmailOtp(
                    email,
                    request.getEmailOtp(),
                    OtpType.EMAIL_VERIFICATION
            );

            user.setEmailVerified(true);
        }

        else if ("PHONE_VERIFICATION".equals(type)) {

            otpService.verifyPhoneOtp(
                    phone,
                    request.getPhoneOtp(),
                    OtpType.PHONE_VERIFICATION
            );

            user.setPhoneNumberVerified(true);
        }

        else if ("BOTH_VERIFICATION".equals(type)) {

            otpService.verifyEmailOtp(
                    email,
                    request.getEmailOtp(),
                    OtpType.EMAIL_VERIFICATION
            );

            otpService.verifyPhoneOtp(
                    phone,
                    request.getPhoneOtp(),
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
