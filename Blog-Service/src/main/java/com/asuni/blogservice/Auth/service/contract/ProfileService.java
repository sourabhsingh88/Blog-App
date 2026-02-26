package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.UpdateUserRequest;
import com.asuni.blogservice.Auth.dto.response.UpdateProfileResponse;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {



    public UpdateProfileResponse updateProfile(
            Long userId,
            UpdateUserRequest request
    );
    void updateProfilePicture(Long userId, MultipartFile profilePicture);

    void updateAadhaar(Long userId, MultipartFile aadhaarImage);

}

