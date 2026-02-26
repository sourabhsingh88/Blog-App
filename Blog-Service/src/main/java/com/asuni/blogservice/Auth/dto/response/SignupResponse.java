package com.asuni.blogservice.Auth.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse {

    private String verificationToken;
    private String phoneOtp; // only for phone
}