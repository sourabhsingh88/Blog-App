package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.SignupRequest;
import com.asuni.blogservice.Auth.dto.response.SignupResponse;
import org.springframework.web.multipart.MultipartFile;

public interface RegistrationService {


    SignupResponse signup(SignupRequest request,
                          MultipartFile aadhaarImage,
                          MultipartFile profilePicture);
    public void hardDeleteAccount(Long userId, String password) ;

//    void softDeleteAccount(User user, String password);


}
