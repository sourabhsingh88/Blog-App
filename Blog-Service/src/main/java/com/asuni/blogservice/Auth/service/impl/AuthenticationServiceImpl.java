package com.asuni.blogservice.Auth.service.impl;

import com.asuni.blogservice.Auth.dto.request.LoginPhoneRequest;
import com.asuni.blogservice.Auth.dto.request.LoginRequest;
import com.asuni.blogservice.Auth.dto.request.RefreshTokenRequest;
import com.asuni.blogservice.Auth.dto.request.VerifyPhoneOtpRequest;
import com.asuni.blogservice.Auth.dto.response.LoginResponse;
import com.asuni.blogservice.Auth.dto.response.PhoneLoginResponse;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.AuthenticationService;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.exceptions.BadRequestException;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import com.asuni.blogservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final OtpService otpService;

    /* ================= LOGIN ================= */

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

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId() , user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());


        // Store refresh token
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return LoginResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .id(user.getId())
                .user_name(user.getUsername())
                .email(user.getEmail())
                .full_name(user.getFullName())
                .phone_number(user.getPhoneNumber())
                .gender(user.getGender())
                .date_of_birth(user.getDateOfBirth())
                .preferred_language(user.getPreferredLanguage())
                .profile_picture_url(user.getProfilePictureUrl())
                .build();
    }

    /* ================= REFRESH ================= */

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {

        String refreshToken = request.getRefresh_token();

        if (!jwtUtil.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        Long userId = jwtUtil.extractUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));


        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token mismatch");
        }

        if (user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new UnauthorizedException("Refresh token expired");
        }

        // Rotate tokens
        String newAccessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getId());


        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return LoginResponse.builder()
                .access_token(newAccessToken)
                .refresh_token(newRefreshToken)
                .id(user.getId())
                .user_name(user.getUsername())
                .email(user.getEmail())
                .full_name(user.getFullName())
                .phone_number(user.getPhoneNumber())
                .gender(user.getGender())
                .date_of_birth(user.getDateOfBirth())
                .build();
    }

    /* ================= PHONE LOGIN ================= */

    @Override
    public PhoneLoginResponse sendPhoneLoginOtp(LoginPhoneRequest request) {

        User user = userRepository.findByPhoneNumber(request.getPhone_number())
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!user.isEmailVerified() || !user.isPhoneNumberVerified()) {
            throw new BadRequestException("Account not verified");
        }

        String phoneOtp = otpService.generatePhoneOtp(
                user.getPhoneNumber(),
                OtpType.PHONE_LOGIN
        );

        String phoneLoginToken = jwtUtil.generateOtpToken(
                user.getEmail(),
                user.getPhoneNumber(),
                "PHONE_LOGIN"
        );

        return PhoneLoginResponse.builder()
                .phoneLoginToken(phoneLoginToken)
                .phoneOtp(phoneOtp)
                .build();
    }

    @Override
    public LoginResponse verifyPhoneLoginOtp(VerifyPhoneOtpRequest request) {

        var claims = jwtUtil.validateAndExtract(request.getPhone_login_token());

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

        // Generate tokens
        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // Store refresh token
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);

        return LoginResponse.builder()
                .access_token(accessToken)
                .refresh_token(refreshToken)
                .id(user.getId())
                .user_name(user.getUsername())
                .email(user.getEmail())
                .full_name(user.getFullName())
                .phone_number(user.getPhoneNumber())
                .gender(user.getGender())
                .date_of_birth(user.getDateOfBirth())
                .preferred_language(user.getPreferredLanguage())
                .profile_picture_url(user.getProfilePictureUrl())
                .build();
    }

    /* ================= INTERNAL ================= */

    @Override
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"))
                .getId();
    }
}
