package com.sourabh.authservice.service.impl;


import com.sourabh.authservice.dto.request.SignupRequest;
import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.enums.OtpType;
import com.sourabh.authservice.exceptions.BadRequestException;
import com.sourabh.authservice.exceptions.UnauthorizedException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.FileStorageService;
import com.sourabh.authservice.service.contract.OtpService;
import com.sourabh.authservice.service.contract.RegistrationService;
import com.sourabh.authservice.util.JwtUtil;
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
    public String signup(SignupRequest request, MultipartFile aadhaarImage) {

        if (request.getPassword() == null || request.getConfirmPassword() == null) {
            throw new BadRequestException("Password and Confirm Password are required");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }


        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }

        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone already registered");
        }

        if (aadhaarImage == null || aadhaarImage.isEmpty()) {
            throw new BadRequestException("Aadhaar image is required");
        }

        if (!aadhaarImage.getContentType().startsWith("image")) {
            throw new BadRequestException("Aadhaar must be an image file");
        }

        // Upload to S3
        String aadhaarUrl = fileStorageService.uploadFile(
                aadhaarImage,
                "aadhar"
        );

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .password(passwordEncoder.encode(request.getPassword()))
                .aadharImageUrl(aadhaarUrl)
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
