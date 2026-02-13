package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.dto.request.SignupRequest;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.service.contract.RegistrationService;
import com.sourabh.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public String signup(SignupRequest request) {

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone already registered");
        }

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .emailVerified(false)
                .phoneNumberVerified(false)
                .build();


        userRepository.save(user);

        // Generate OTPs
        otpService.generateEmailOtp(user.getEmail(), OtpType.EMAIL_VERIFICATION);
        otpService.generatePhoneOtp(user.getPhoneNumber(), OtpType.PHONE_VERIFICATION);

        // Generate verification token
        return jwtUtil.generateOtpToken(
                user.getEmail(),
                user.getPhoneNumber(),
                "BOTH_VERIFICATION"
        );

    }
}
