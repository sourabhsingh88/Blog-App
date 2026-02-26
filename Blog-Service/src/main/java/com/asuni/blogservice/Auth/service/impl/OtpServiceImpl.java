package com.asuni.blogservice.Auth.service.impl;

import com.asuni.blogservice.Auth.entity.Otp;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.OtpRepository;
import com.asuni.blogservice.Auth.service.contract.EmailService;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private final OtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int RESEND_COOLDOWN_SECONDS = 60;
    private static final int MAX_ATTEMPTS = 5;

    /* ===================== GENERATE ===================== */

    @Override
    public void generateEmailOtp(String email, OtpType type) {
        createOtp(email, null, type);
    }

    @Override
    public String generatePhoneOtp(String phone, OtpType type) {
        return createOtp(null, phone, type);
    }

    private String createOtp(String email, String phone, OtpType type) {

        LocalDateTime now = LocalDateTime.now();

        Otp lastOtp = null;

        if (email != null) {
            lastOtp = otpRepository
                    .findValidEmailOtp(email, type, now)
                    .orElse(null);
        }

        if (phone != null) {
            lastOtp = otpRepository
                    .findValidPhoneOtp(phone, type, now)
                    .orElse(null);
        }

        if (lastOtp != null &&
                lastOtp.getCreatedAt().isAfter(now.minusSeconds(RESEND_COOLDOWN_SECONDS))) {
            throw new BadRequestException("Please wait before requesting another OTP");
        }

        String rawOtp = generateOtp();

        Otp otp = Otp.builder()
                .email(email)
                .phone(phone)
                .type(type)
                .otpHash(passwordEncoder.encode(rawOtp))
                .attempts(0)
                .verified(false)
                .expiry(now.plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRepository.save(otp);

        // Email → send only
        if (email != null) {
            emailService.sendOtp(email, rawOtp);
            return null;
        }

        // Phone → return OTP
        if (phone != null) {
            return rawOtp;
        }

        return null;
    }

    /* ===================== VERIFY ===================== */

    @Override
    public void verifyEmailOtp(String email, String otp, OtpType type) {
        Otp entity = otpRepository
                .findValidEmailOtp(email, type, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("OTP expired or invalid"));

        validateOtp(entity, otp);
    }

    @Override
    public void verifyPhoneOtp(String phone, String otp, OtpType type) {
        Otp entity = otpRepository
                .findValidPhoneOtp(phone, type, LocalDateTime.now())
                .orElseThrow(() -> new BadRequestException("OTP expired or invalid"));
        validateOtp(entity, otp);
    }

    private void validateOtp(Otp otp, String rawOtp) {

        if (otp.getAttempts() >= MAX_ATTEMPTS) {
            throw new BadRequestException("OTP attempts exceeded");
        }

        if (!passwordEncoder.matches(rawOtp, otp.getOtpHash())) {
            otp.setAttempts(otp.getAttempts() + 1);
            otpRepository.save(otp);
            throw new BadRequestException("Invalid OTP");
        }

        otp.setVerified(true);
        otpRepository.save(otp);
    }

    private String generateOtp() {
        return String.valueOf(100000 + new Random().nextInt(900000));
    }
}
