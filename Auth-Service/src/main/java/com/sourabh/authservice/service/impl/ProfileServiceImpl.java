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

import java.util.Map;

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

        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
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

        if (request.getPhoneNumber() != null &&
                !request.getPhoneNumber().equals(user.getPhoneNumber())) {

            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
                throw new BadRequestException("Phone number already in use");
            }

            user.setPhoneNumber(request.getPhoneNumber());
            user.setPhoneNumberVerified(false);

            otpService.generatePhoneOtp(
                    request.getPhoneNumber(),
                    OtpType.PHONE_VERIFICATION
            );

            phoneChanged = true;
        }

        // -------- AADHAAR UPDATE --------

        if (request.getAadhaarImage() != null &&
                !request.getAadhaarImage().isEmpty()) {

            if (!request.getAadhaarImage().getContentType().startsWith("image")) {
                throw new BadRequestException("Aadhaar must be an image file");
            }

            String aadhaarUrl = fileStorageService.uploadFile(
                    request.getAadhaarImage(),
                    "aadhar"
            );

            user.setAadharImageUrl(aadhaarUrl);
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

//    @Override
//    public String updateProfile(User user, UpdateUserRequest request) {
//
//        boolean emailChanged = false;
//        boolean phoneChanged = false;
//
//        // -------- NON-SENSITIVE --------
//
//        if (request.getFullName() != null) {
//            user.setFullName(request.getFullName());
//        }
//
//        if (request.getGender() != null) {
//            user.setGender(request.getGender());
//        }
//
//        if (request.getDateOfBirth() != null) {
//            user.setDateOfBirth(request.getDateOfBirth());
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
//        if (request.getPhoneNumber() != null &&
//                !request.getPhoneNumber().equals(user.getPhoneNumber())) {
//
//            if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
//                throw new BadRequestException("Phone number already in use");
//            }
//
//            user.setPhoneNumber(request.getPhoneNumber());
//            user.setPhoneNumberVerified(false);
//
//            otpService.generatePhoneOtp(
//                    request.getPhoneNumber(),
//                    OtpType.PHONE_VERIFICATION
//            );
//
//            phoneChanged = true;
//        }
//
////        ---------------------------------------------------------------
//        // -------- AADHAAR UPDATE --------
//
//        if (request.getAadhaarImage() != null &&
//                !request.getAadhaarImage().isEmpty()) {
//
//            if (!request.getAadhaarImage().getContentType().startsWith("image")) {
//                throw new BadRequestException("Aadhaar must be an image file");
//            }
//
//            // -------- AADHAAR UPDATE --------
//
//            if (request.getAadhaarImage() != null &&
//                    !request.getAadhaarImage().isEmpty()) {
//
//                if (!request.getAadhaarImage().getContentType().startsWith("image")) {
//                    throw new BadRequestException("Aadhaar must be an image file");
//                }
//
//                String aadhaarUrl = fileStorageService.uploadFile(
//                        request.getAadhaarImage(),
//                        "aadhar"
//                );
//
//                user.setAadharImageUrl(aadhaarUrl);
//            }
//
//        }
//
//        //        ---------------------------------------------------------------
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
//                    user.getPhoneNumber(),
//                    type
//            );
//        }
//
//        return null;
//    }
}
