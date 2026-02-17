package com.sourabh.authservice.service.impl;


import com.sourabh.authservice.dto.request.UpdateUserRequest;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.FileStorageService;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.service.contract.ProfileService;
import com.sourabh.authservice.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public String updateProfile(Long userId, UpdateUserRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));

        boolean emailChanged = false;
        boolean phoneChanged = false;

        // -------- NON-SENSITIVE --------

        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
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

        // -------- PROFILE PICTURE UPDATE --------

        if (request.getProfile_picture() != null &&
                !request.getProfile_picture().isEmpty()) {

            if (!request.getProfile_picture().getContentType().startsWith("image")) {
                throw new BadRequestException("Profile picture must be an image");
            }

            String profileUrl = fileStorageService.uploadFile(
                    request.getProfile_picture(),
                    "profile"
            );

            user.setProfilePictureUrl(profileUrl);
        }

        // -------- AADHAAR UPDATE --------

        if (request.getAadhaar_image() != null &&
                !request.getAadhaar_image().isEmpty()) {

            if (!request.getAadhaar_image().getContentType().startsWith("image")) {
                throw new BadRequestException("Aadhaar must be an image file");
            }

            String aadhaarUrl = fileStorageService.uploadFile(
                    request.getAadhaar_image(),
                    "aadhaar"
            );

            user.setAadharImageUrl(aadhaarUrl);
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

            otpService.generatePhoneOtp(
                    request.getPhone_number(),
                    OtpType.PHONE_VERIFICATION
            );

            phoneChanged = true;
        }

        userRepository.save(user);

        // -------- TOKEN GENERATION --------

        if (emailChanged || phoneChanged) {

            String type;

            if (emailChanged && phoneChanged) {
                type = "BOTH_VERIFICATION";
            } else if (emailChanged) {
                type = "EMAIL_VERIFICATION";
            } else {
                type = "PHONE_VERIFICATION";
            }

            return jwtUtil.generateOtpToken(
                    user.getEmail(),
                    user.getPhoneNumber(),
                    type
            );
        }

        return null;
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
