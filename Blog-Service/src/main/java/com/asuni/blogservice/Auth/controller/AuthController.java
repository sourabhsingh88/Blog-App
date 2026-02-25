package com.asuni.blogservice.Auth.controller;

import com.asuni.blogservice.Auth.dto.request.*;
import com.asuni.blogservice.Auth.dto.response.LoginResponse;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.service.contract.*;
import com.asuni.blogservice.Auth.service.contract.AuthenticationService;
import com.asuni.blogservice.Auth.service.contract.ProfileService;
import com.asuni.blogservice.Auth.service.contract.RegistrationService;
import com.asuni.blogservice.Auth.service.contract.VerificationService;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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




    /* ===================== Signup ===================== */

//    @Operation(summary = "User Signup")
//    @PostMapping(
//            value = "/signup",
//            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<?> signup(
//            @ParameterObject @ModelAttribute SignupRequest request,
//            @RequestParam("aadharImage") MultipartFile aadharImage
//    ) {
//
//        String verification_token =
//                registrationService.signup(request, aadharImage);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(Map.of(
//                        "message", "User registered successfully",
//                        "verification_token", verification_token
//                ));
//    }

    @Operation(summary = "User Signup")
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> signup(
            @ParameterObject @ModelAttribute SignupRequest request,
            @RequestParam("aadhaar_image") MultipartFile aadhaarImage,
            @RequestParam("profile_picture") MultipartFile profilePicture
    ) {

        String verificationToken =
                registrationService.signup(request, aadhaarImage, profilePicture);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "verification_token", verificationToken
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

    /* ===================== Update ===================== */
    @PatchMapping(
            value = "/update",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @ModelAttribute UpdateUserRequest request
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Long userId;
        try {
            userId = Long.parseLong(authentication.getName());
        } catch (NumberFormatException ex) {
            throw new UnauthorizedException("Invalid authentication token");
        }

        String verificationToken =
                profileService.updateProfile(userId, request);

        if (verificationToken != null) {
            return ResponseEntity
                    .status(HttpStatus.ACCEPTED)
                    .body(Map.of(
                            "message", "Email/Phone verification required",
                            "verification_token", verificationToken
                    ));
        }

        return ResponseEntity.ok(
                Map.of("message", "Profile updated successfully")
        );
    }


//    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> updateProfile(
//            Authentication authentication,
//            @ModelAttribute UpdateUserRequest request
//    ) {
//
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new UnauthorizedException("Unauthorized");
//        }
//
//        Long userId = Long.valueOf(authentication.getName());
//
//        String verification_token = profileService.updateProfile(userId, request);
//
//        if (verification_token != null) {
//            return ResponseEntity
//                    .status(HttpStatus.ACCEPTED)
//                    .body(Map.of(
//                            "message", "Verification required",
//                            "verification_token", verification_token
//                    ));
//        }
//
//        return ResponseEntity.ok(
//                Map.of("message", "Profile updated successfully")
//        );
//    }


    /* ===================== LOGIN (PHONE OTP) ===================== */
    @PostMapping("/login/phone")
    public ResponseEntity<?> sendPhoneOtp(
            @Valid @RequestBody LoginPhoneRequest request
    ) {
        String phoneLoginToken = authenticationService.sendPhoneLoginOtp(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("phone_login_token", phoneLoginToken));
    }

    /* ===================== VERIFY PHONE OTP  ===================== */
    @PostMapping("/login/phone/verify")
    public ResponseEntity<?> verifyPhoneOtp(
            @Valid @RequestBody VerifyPhoneOtpRequest request
    ) {
        String authToken = authenticationService.verifyPhoneLoginOtp(request);

        return ResponseEntity.ok(
                Map.of("token", authToken)
        );
    }

    /* ===================== FORGOT PASSWORD - EMAIl OTP  1 ===================== */
    @PostMapping("/password/forgot")
    public ResponseEntity<?> forgotPassword(
            @Valid @RequestBody ResetPasswordOtpRequest request
    ) {
        String resetOtpToken = passwordService.forgotPasswordOtp(request);

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(Map.of("reset_otp_token", resetOtpToken));
    }

    /* ===================== VERIFY RESET OTP  2 ===================== */
    @PostMapping("/password/verify-otp")
    public ResponseEntity<?> verifyResetOtp(
            @Valid @RequestBody VerifyResetOtpRequest request
    ) {
        String resetPasswordToken = passwordService.verifyResetOtp(request);

        return ResponseEntity.ok(
                Map.of("reset_password_token", resetPasswordToken)
        );
    }

    /* ===================== RESET PASSWORD  3 ===================== */

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
            Authentication authentication,
            @Valid @RequestBody ChangePasswordRequest request
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Long userId = Long.parseLong(authentication.getName());

        passwordService.changePassword(userId, request);

        return ResponseEntity.ok(
                Map.of("message", "Password changed successfully")
        );
    }
    /* ===================== HARD DELETE ACCOUNT ===================== */

    @DeleteMapping("/delete/hard")
    public ResponseEntity<?> hardDeleteAccount(
            Authentication authentication,
            @Valid @RequestBody ConfirmPasswordRequest request
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Long userId = Long.valueOf(authentication.getName());

        registrationService.hardDeleteAccount(userId, request.getPassword());

        return ResponseEntity.ok(
                Map.of("message", "Account permanently deleted")
        );
    }


    /* ===================== SOFT DELETE ACCOUNT ===================== */

//    @DeleteMapping("/delete/soft")
//    public ResponseEntity<?> softDeleteAccount(
//            @AuthenticationPrincipal User user,
//            @Valid @RequestBody ConfirmPasswordRequest request
//    ) {
//        if (user == null) {
//            throw new UnauthorizedException("Unauthorized");
//        }
//
//        registrationService.softDeleteAccount(user, request.getPassword());
//
//        return ResponseEntity.ok(
//                Map.of("message", "Account deactivated successfully")
//        );
//    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(
            @RequestBody RefreshTokenRequest request
    ) {
        return ResponseEntity.ok(authenticationService.refreshToken(request));
    }

}
