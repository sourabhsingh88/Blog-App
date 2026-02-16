package com.sourabh.authservice.controller;

import com.sourabh.authservice.dto.request.*;
import com.sourabh.authservice.dto.response.LoginResponse;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.exceptions.UnauthorizedException;
import com.sourabh.authservice.service.contract.*;
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





    /* ===================== SIGNUP ===================== */
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
//
//        String verificationToken = registrationService.signup(request);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(Map.of(
//                        "message", "User registered successfully",
//                        "verificationToken", verificationToken
//                ));
//    }
//    @PostMapping(value = "/signup", consumes = "multipart/form-data")
//    public ResponseEntity<?> signup(
//            @ModelAttribute SignupRequest request,
//            @RequestParam("aadharImage") MultipartFile aadharImage
//    ) {
//
//        String verificationToken =
//                registrationService.signup(request, aadharImage);
//
//        return ResponseEntity
//                .status(HttpStatus.CREATED)
//                .body(Map.of(
//                        "message", "User registered successfully",
//                        "verificationToken", verificationToken
//                ));
//    }
//
//
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

    @Operation(summary = "User Signup")
    @PostMapping(
            value = "/signup",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> signup(
            @ParameterObject @ModelAttribute SignupRequest request,
            @RequestParam("aadharImage") MultipartFile aadharImage
    ) {

        String verificationToken =
                registrationService.signup(request, aadharImage);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "User registered successfully",
                        "verificationToken", verificationToken
                ));
    }

    /* ===================== LOGIN (EMAIL) ===================== */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        return ResponseEntity.ok(
                authenticationService.login(request)
        );
    }

    /* ===================== UPDATE PROFILE ===================== */
//    @PatchMapping(value = "/update", consumes = "multipart/form-data")
//    public ResponseEntity<?> updateProfile(
//            @AuthenticationPrincipal User user,
//            @ModelAttribute UpdateUserRequest request
//    ) {
//        if (user == null) {
//            throw new UnauthorizedException("Unauthorized");
//        }
//
//        String verificationToken = profileService.updateProfile(user, request);
//
//        if (verificationToken != null) {
//            return ResponseEntity
//                    .status(HttpStatus.ACCEPTED)
//                    .body(Map.of(
//                            "message", "Verification required",
//                            "verificationToken", verificationToken
//                    ));
//        }
//
//        return ResponseEntity.ok(
//                Map.of("message", "Profile updated successfully")
//        );
//    }
    @PatchMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            Authentication authentication,
            @ModelAttribute UpdateUserRequest request
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Unauthorized");
        }

        Long userId = Long.valueOf(authentication.getName());

        String verificationToken = profileService.updateProfile(userId, request);

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
