package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.SignupRequest;
import com.sourabh.authservice.entity.User;
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
