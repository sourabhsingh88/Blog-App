package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.dto.response.LoginResponse;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.exceptions.NotFoundException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.AuthenticationService;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    @Override
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BadRequestException("Invalid credentials");
        }

        if (!user.isEmailVerified() || !user.isPhoneNumberVerified()) {
            throw new BadRequestException("Account not verified");
        }

        return LoginResponse.builder()
                .token(jwtUtil.generateAuthToken(user.getEmail()))
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .dateOfBirth(user.getDateOfBirth())
                .build();
    }

    @Override
    public String sendPhoneLoginOtp(LoginPhoneRequest request) {

        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEmailVerified() || !user.isPhoneNumberVerified()) {
            throw new BadRequestException("Account not verified");
        }

        otpService.generatePhoneOtp(
                user.getPhoneNumber(),
                OtpType.PHONE_LOGIN
        );

        return jwtUtil.generateOtpToken(
                user.getEmail(),
                user.getPhoneNumber(),
                "PHONE_LOGIN"
        );
    }

    @Override
    public String verifyPhoneLoginOtp(VerifyPhoneOtpRequest request) {

        var claims = jwtUtil.validateAndExtract(request.getPhoneLoginToken());

        String type = claims.get("type", String.class);

        if (!"PHONE_LOGIN".equals(type)) {
            throw new BadRequestException("Invalid token type");
        }

        String phone = claims.get("phone", String.class);

        otpService.verifyPhoneOtp(
                phone,
                request.getOtp(),
                OtpType.PHONE_LOGIN
        );

        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return jwtUtil.generateAuthToken(user.getEmail());
    }
}
