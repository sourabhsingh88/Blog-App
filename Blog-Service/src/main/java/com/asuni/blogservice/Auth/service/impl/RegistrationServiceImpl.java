package com.asuni.blogservice.Auth.service.impl;


import com.asuni.blogservice.Auth.dto.request.SignupRequest;
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
    public String signup(SignupRequest request,
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

        if (aadhaarImage == null || aadhaarImage.isEmpty()) {
            throw new BadRequestException("Aadhaar image is required");
        }

        if (profilePicture == null || profilePicture.isEmpty()) {
            throw new BadRequestException("Profile picture is required");
        }

        if (!aadhaarImage.getContentType().startsWith("image")) {
            throw new BadRequestException("Aadhaar must be an image");
        }

        if (!profilePicture.getContentType().startsWith("image")) {
            throw new BadRequestException("Profile picture must be an image");
        }

        String aadhaarUrl = fileStorageService.uploadFile(aadhaarImage, "aadhaar");
        String profileUrl = fileStorageService.uploadFile(profilePicture, "profile");

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
        otpService.generatePhoneOtp(user.getPhoneNumber(), OtpType.PHONE_VERIFICATION);

        return jwtUtil.generateOtpToken(
                user.getEmail(),
                user.getPhoneNumber(),
                "BOTH_VERIFICATION"
        );
    }

//    @Override
//    @Transactional
//    public String signup(SignupRequest request, MultipartFile aadhaar_image) {
//
//        if (request.getPassword() == null || request.getConfirm_password() == null) {
//            throw new BadRequestException("Password and Confirm Password are required");
//        }
//
//        if (!request.getPassword().equals(request.getConfirm_password())) {
//            throw new BadRequestException("Passwords do not match");
//        }
//
//
//        if (userRepository.existsByEmail(request.getEmail())) {
//            throw new BadRequestException("Email already registered");
//        }
//
//        if (userRepository.existsByPhoneNumber(request.getPhone_number())) {
//            throw new BadRequestException("Phone already registered");
//        }
//
//        if (aadhaar_image == null || aadhaar_image.isEmpty()) {
//            throw new BadRequestException("Aadhaar image is required");
//        }
//
//        if (!aadhaar_image.getContentType().startsWith("image")) {
//            throw new BadRequestException("Aadhaar must be an image file");
//        }
//
//        // Upload to S3
//        String aadhaarUrl = fileStorageService.uploadFile(
//                aadhaar_image,
//                "aadhar"
//        );
//
//        User user = User.builder()
//                .email(request.getEmail())
//                .full_name(request.getFull_name())
//                .username(request.getUsername())
//                .phone_number(request.getPhone_number())
//                .gender(request.getGender())
//                .date_of_birth(request.getDate_of_birth())
//                .password(passwordEncoder.encode(request.getPassword()))
//                .aadharImageUrl(aadhaarUrl)
//                .emailVerified(false)
//                .phoneNumberVerified(false)
//                .build();
//
//        userRepository.save(user);
//
//        otpService.generateEmailOtp(user.getEmail(), OtpType.EMAIL_VERIFICATION);
//        otpService.generatePhoneOtp(user.getPhone_number(), OtpType.PHONE_VERIFICATION);
//
//        return jwtUtil.generateOtpToken(
//                user.getEmail(),
//                user.getPhone_number(),
//                "BOTH_VERIFICATION"
//        );
//    }

    @Override
    @Transactional
    public void hardDeleteAccount(Long userId, String password) {

        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, existingUser.getPassword())) {
            throw new UnauthorizedException("Invalid password");
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
