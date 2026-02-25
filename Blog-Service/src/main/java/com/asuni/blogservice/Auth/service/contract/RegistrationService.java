package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.SignupRequest;
import org.springframework.web.multipart.MultipartFile;

public interface RegistrationService {

    /**
     * Registers user and returns verification token
     */
    String signup(SignupRequest request,
                  MultipartFile aadhaarImage,
                  MultipartFile profilePicture);
    public void hardDeleteAccount(Long userId, String password) ;

//    void softDeleteAccount(User user, String password);


}
