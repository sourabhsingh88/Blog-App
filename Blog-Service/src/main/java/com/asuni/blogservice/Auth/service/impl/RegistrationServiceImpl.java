package com.asuni.blogservice.Auth.service.impl;


import com.asuni.blogservice.Auth.dto.request.SignupRequest;
import com.asuni.blogservice.Auth.dto.response.SignupResponse;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.FileStorageService;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.Auth.service.contract.RegistrationService;
import com.asuni.blogservice.exceptions.BadRequestException;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import com.asuni.blogservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public SignupResponse signup(SignupRequest request,
                                 MultipartFile aadhaarImage,
                                 MultipartFile profilePicture) {

        if (!request.getPassword().equals(request.getConfirm_password())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhone_number())) {
            throw new BadRequestException("Phone already registered");
        }

        if (userRepository.existsByUsername(request.getUser_name())) {
            throw new BadRequestException("Username already taken");
        }

//        if (aadhaarImage == null || aadhaarImage.isEmpty()) {
//            throw new BadRequestException("Aadhaar image is required");
//        }
//
//        if (profilePicture == null || profilePicture.isEmpty()) {
//            throw new BadRequestException("Profile picture is required");
//        }

        String aadhaarUrl = null;
        String profileUrl = null;

        if (aadhaarImage != null && !aadhaarImage.isEmpty()) {
            if (aadhaarImage.getContentType() == null ||
                    !aadhaarImage.getContentType().startsWith("image/")) {
                throw new BadRequestException("Aadhaar must be an image");
            }
            aadhaarUrl = fileStorageService.uploadFile(aadhaarImage, "aadhaar");
        }

        if (profilePicture != null && !profilePicture.isEmpty()) {
            if (profilePicture.getContentType() == null ||
                    !profilePicture.getContentType().startsWith("image/")) {
                throw new BadRequestException("Profile picture must be an image");
            }
            profileUrl = fileStorageService.uploadFile(profilePicture, "profile");
        }


        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFull_name())
                .username(request.getUser_name())
                .phoneNumber(request.getPhone_number())
                .gender(request.getGender())
                .dateOfBirth(request.getDate_of_birth())
                .preferredLanguage(request.getPreferred_language())
                .password(passwordEncoder.encode(request.getPassword()))
                .aadharImageUrl(aadhaarUrl)
                .profilePictureUrl(profileUrl)
                .emailVerified(false)
                .phoneNumberVerified(false)
                .build();

        userRepository.save(user);

        otpService.generateEmailOtp(user.getEmail(), OtpType.EMAIL_VERIFICATION);

        String phoneOtp =
                otpService.generatePhoneOtp(user.getPhoneNumber(), OtpType.PHONE_VERIFICATION);

        String verificationToken = jwtUtil.generateOtpToken(
                user.getEmail(),
                user.getPhoneNumber(),
                "BOTH_VERIFICATION"
        );

        return SignupResponse.builder()
                .verificationToken(verificationToken)
                .phoneOtp(phoneOtp)
                .build();
    }


    @Override
    @Transactional
    public void hardDeleteAccount(Long userId, String password) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));


        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        if (existingUser.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(existingUser.getProfilePictureUrl());
        }

        if (existingUser.getAadharImageUrl() != null) {
            fileStorageService.deleteFile(existingUser.getAadharImageUrl());
        }

        userRepository.delete(existingUser);

    }

//    @Override
//    @Transactional
//    public void softDeleteAccount(User user, String password) {
//
//        User existingUser = userRepository.findById(user.getId())
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
//            throw new UnauthorizedException("Invalid password");
//        }
//
//        existingUser.setDeleted(true);
//        existingUser.setEmailVerified(false);
//        existingUser.setPhoneNumberVerified(false);
//
//        userRepository.save(existingUser);
//    }

}
