package com.asuni.blogservice.Auth.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateProfileResponse {
    private String verificationToken;
    private String phoneOtp;

}
