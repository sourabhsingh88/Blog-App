package com.sourabh.authservice.controller;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.dto.response.LoginResponse;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.exceptions.UnauthorizedException;
import com.sourabh.authservice.service.contract.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final RegistrationService registrationService;
    private final VerificationService verificationService;
    private final AuthenticationService authenticationService;
    private final ProfileService profileService;
    private final PasswordService passwordService;





    /* ===================== SIGNUP ===================== */
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {

        String verificationToken = registrationService.signup(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "verificationToken", verificationToken
                ));
    }

    /* ===================== VERIFY ACCOUNT ===================== */
    @PostMapping("/verify")
    public ResponseEntity<?> verifyAccount(
            @Valid @RequestBody VerifyAccountRequest request
    ) {
        verificationService.verifyAccount(request);

        return ResponseEntity.ok(
                Map.of("message", "Account verified successfully")
        );
    }

    /* ===================== LOGIN (EMAIL) ===================== */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }

    /* ===================== UPDATE PROFILE ===================== */
    @PatchMapping("/update")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        if (user == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        String verificationToken = profileService.updateProfile(user, request);

        if (verificationToken != null) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "message", "Verification required",
                            "verificationToken", verificationToken
                    ));
        }

        return ResponseEntity.ok(
                Map.of("message", "Profile updated successfully")
        );
    }

    /* ===================== LOGIN (PHONE OTP - STEP 1) ===================== */
    @PostMapping("/login/phone")
    public ResponseEntity<?> sendPhoneOtp(
            @Valid @RequestBody LoginPhoneRequest request
    ) {
        String phoneLoginToken = authenticationService.sendPhoneLoginOtp(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("phoneLoginToken", phoneLoginToken));
    }

    /* ===================== VERIFY PHONE OTP - STEP 2) ===================== */
    @PostMapping("/login/phone/verify")
    public ResponseEntity<?> verifyPhoneOtp(
            @Valid @RequestBody VerifyPhoneOtpRequest request
    ) {
        String authToken = authenticationService.verifyPhoneLoginOtp(request);

        return ResponseEntity.ok(
                Map.of("token", authToken)
        );
    }

    /* ===================== FORGOT PASSWORD - STEP 1 ===================== */
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ResetPasswordOtpRequest request
    ) {
        String resetOtpToken = passwordService.forgotPasswordOtp(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("resetOtpToken", resetOtpToken));
    }

    /* ===================== VERIFY RESET OTP - STEP 2 ===================== */
    @PostMapping("/password/verify-otp")
    public ResponseEntity<?> verifyResetOtp(
            @Valid @RequestBody VerifyResetOtpRequest request
    ) {
        String resetPasswordToken = passwordService.verifyResetOtp(request);

        return ResponseEntity.ok(
                Map.of("resetPasswordToken", resetPasswordToken)
        );
    }

    /* ===================== RESET PASSWORD - STEP 3 ===================== */
    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        passwordService.resetPassword(request);

        return ResponseEntity.ok(
                Map.of("message", "Password reset successful")
        );
    }

    /* ===================== CHANGE PASSWORD ===================== */
    @PostMapping("/password/change")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ChangePasswordRequest request
    ) {
        if (user == null) {
            throw new UnauthorizedException("Unauthorized");
        }

        passwordService.changePassword(user, request);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );

    }
    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

}
