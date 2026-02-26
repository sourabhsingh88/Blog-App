package com.asuni.blogservice.Auth.service.impl;


import com.asuni.blogservice.Auth.dto.request.UpdateUserRequest;
import com.asuni.blogservice.Auth.dto.response.UpdateProfileResponse;
import com.asuni.blogservice.Auth.dto.response.UserResponse;
import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.enums.OtpType;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.FileStorageService;
import com.asuni.blogservice.Auth.service.contract.OtpService;
import com.asuni.blogservice.Auth.service.contract.ProfileService;
import com.asuni.blogservice.exceptions.BadRequestException;
import com.asuni.blogservice.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return mapToResponse(user);
    }
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    @Override
    @Transactional
    public UpdateProfileResponse updateProfile(
            Long userId,
            UpdateUserRequest request
    ) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        boolean emailChanged = false;
        boolean phoneChanged = false;

        String phoneOtp = null;

        // -------- NON-SENSITIVE --------

        if (request.getFull_Name() != null) {
            user.setFullName(request.getFull_Name());
        }

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }

        if (request.getDate_of_birth() != null) {
            user.setDateOfBirth(request.getDate_of_birth());
        }

        if (request.getPreferred_language() != null) {
            user.setPreferredLanguage(request.getPreferred_language());
        }

        // -------- EMAIL CHANGE --------

        if (request.getEmail() != null &&
                !request.getEmail().equals(user.getEmail())) {

            if (userRepository.existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already in use");
            }

            user.setEmail(request.getEmail());
            user.setEmailVerified(false);

            otpService.generateEmailOtp(
                    request.getEmail(),
                    OtpType.EMAIL_VERIFICATION
            );

            emailChanged = true;
        }

        // -------- PHONE CHANGE --------

        if (request.getPhone_number() != null &&
                !request.getPhone_number().equals(user.getPhoneNumber())) {

            if (userRepository.existsByPhoneNumber(request.getPhone_number())) {
                throw new BadRequestException("Phone number already in use");
            }

            user.setPhoneNumber(request.getPhone_number());
            user.setPhoneNumberVerified(false);

            phoneOtp = otpService.generatePhoneOtp(
                    request.getPhone_number(),
                    OtpType.PHONE_VERIFICATION
            );

            phoneChanged = true;
        }

        userRepository.save(user);

        if (emailChanged || phoneChanged) {

            String type;

            if (emailChanged && phoneChanged) {
                type = "BOTH_VERIFICATION";
            } else if (emailChanged) {
                type = "EMAIL_VERIFICATION";
            } else {
                type = "PHONE_VERIFICATION";
            }

            String verificationToken = jwtUtil.generateOtpToken(
                    user.getEmail(),
                    user.getPhoneNumber(),
                    type
            );

            return new UpdateProfileResponse(verificationToken, phoneOtp);
        }

        return null;
    }
    @Override
    @Transactional
    public void updateProfilePicture(Long userId, MultipartFile profilePicture) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new BadRequestException("Profile picture is required");
        }

        String contentType = profilePicture.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Profile picture must be an image");
        }

        String newKey = fileStorageService.uploadFile(profilePicture, "profile");

        if (user.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(user.getProfilePictureUrl());
        }

        user.setProfilePictureUrl(newKey);


        userRepository.save(user);
    }
    @Override
    @Transactional
    public void updateAadhaar(Long userId, MultipartFile aadhaarImage) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (aadhaarImage == null || aadhaarImage.isEmpty()) {
            throw new BadRequestException("Aadhaar image is required");
        }

        String contentType = aadhaarImage.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new BadRequestException("Aadhaar must be an image");
        }

        String newKey = fileStorageService.uploadFile(aadhaarImage, "aadhaar");

        if (user.getAadharImageUrl() != null) {
            fileStorageService.deleteFile(user.getAadharImageUrl());
        }

        user.setAadharImageUrl(newKey);


        userRepository.save(user);
    }
    @Override
    @Transactional
    public void removeAadhaar(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getAadharImageUrl() != null) {
            fileStorageService.deleteFile(user.getAadharImageUrl());
            user.setAadharImageUrl(null);
            user.setAadhaarVerified(false);
        }

        userRepository.save(user);
    }



    @Override
    @Transactional
    public void removeProfilePicture(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (user.getProfilePictureUrl() != null) {
            fileStorageService.deleteFile(user.getProfilePictureUrl());
            user.setProfilePictureUrl(null);
        }

        userRepository.save(user);
    }

    private UserResponse mapToResponse(User user) {

        return UserResponse.builder()
                .id(user.getId())
                .user_name(user.getUsername())
                .email(user.getEmail())
                .full_name(user.getFullName())
                .phone_number(user.getPhoneNumber())
                .gender(user.getGender())
                .date_of_birth(user.getDateOfBirth())
                .preferred_language(user.getPreferredLanguage())
                .profile_picture_url(user.getProfilePictureUrl())
                .email_verified(user.isEmailVerified())
                .phone_number_verified(user.isPhoneNumberVerified())
                .build();
    }



}

