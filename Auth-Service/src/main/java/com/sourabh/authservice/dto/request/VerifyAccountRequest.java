package com.sourabh.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyAccountRequest {

    @NotBlank
    private String verification_token;


    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String email_otp;


    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String phone_otp;
}
