package com.asuni.blogservice.Auth.service.impl;


import com.asuni.blogservice.Auth.dto.request.UpdateUserRequest;
import com.asuni.blogservice.Auth.dto.response.UpdateProfileResponse;
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


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

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

        String profileUrl = fileStorageService.uploadFile(profilePicture, "profile");
        user.setProfilePictureUrl(profileUrl);

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

        String aadhaarUrl = fileStorageService.uploadFile(aadhaarImage, "aadhaar");
        user.setAadharImageUrl(aadhaarUrl);

        userRepository.save(user);
    }
}

//
//    @Override
//    @Transactional
//    public String updateProfile(Long userId, UpdateUserRequest request) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new BadRequestException("User not found"));
//
//        boolean emailChanged = false;
//        boolean phoneChanged = false;
//
//        // -------- NON-SENSITIVE --------
//
//        if (request.getFull_name() != null) {
//            user.setFull_name(request.getFull_name());
//        }
//
//        if (request.getGender() != null) {
//            user.setGender(request.getGender());
//        }
//
//        if (request.getDate_of_birth() != null) {
//            user.setDate_of_birth(request.getDate_of_birth());
//        }
//
//        // -------- EMAIL CHANGE --------
//
//        if (request.getEmail() != null &&
//                !request.getEmail().equals(user.getEmail())) {
//
//            if (userRepository.existsByEmail(request.getEmail())) {
//                throw new BadRequestException("Email already in use");
//            }
//
//            user.setEmail(request.getEmail());
//            user.setEmailVerified(false);
//
//            otpService.generateEmailOtp(
//                    request.getEmail(),
//                    OtpType.EMAIL_VERIFICATION
//            );
//
//            emailChanged = true;
//        }
//
//        // -------- PHONE CHANGE --------
//
//        if (request.getPhone_number() != null &&
//                !request.getPhone_number().equals(user.getPhone_number())) {
//
//            if (userRepository.existsByPhoneNumber(request.getPhone_number())) {
//                throw new BadRequestException("Phone number already in use");
//            }
//
//            user.setPhone_number(request.getPhone_number());
//            user.setPhoneNumberVerified(false);
//
//            otpService.generatePhoneOtp(
//                    request.getPhone_number(),
//                    OtpType.PHONE_VERIFICATION
//            );
//
//            phoneChanged = true;
//        }
//
//        // -------- AADHAAR UPDATE --------
//
//        if (request.getAadhaar_image() != null &&
//                !request.getAadhaar_image().isEmpty()) {
//
//            if (!request.getAadhaar_image().getContentType().startsWith("image")) {
//                throw new BadRequestException("Aadhaar must be an image file");
//            }
//
//            String aadhaarUrl = fileStorageService.uploadFile(
//                    request.getAadhaar_image(),
//                    "aadhar"
//            );
//
//            user.setAadharImageUrl(aadhaarUrl);
//        }
//
//        userRepository.save(user);
//
//        // -------- TOKEN GENERATION --------
//
//        if (emailChanged || phoneChanged) {
//
//            String type;
//
//            if (emailChanged && phoneChanged) {
//                type = "BOTH_VERIFICATION";
//            } else if (emailChanged) {
//                type = "EMAIL_VERIFICATION";
//            } else {
//                type = "PHONE_VERIFICATION";
//            }
//
//            return jwtUtil.generateOtpToken(
//                    user.getEmail(),
//                    user.getPhone_number(),
//                    type
//            );
//        }
//
//        return null;
//    }
//}
