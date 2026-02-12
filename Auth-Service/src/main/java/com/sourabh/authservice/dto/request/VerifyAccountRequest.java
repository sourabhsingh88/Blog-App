package com.sourabh.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyAccountRequest {

    @NotBlank
    private String verificationToken;


    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String emailOtp;


    @Pattern(regexp = "^\\d{6}$", message = "OTP must be 6 digits")
    private String phoneOtp;
}
