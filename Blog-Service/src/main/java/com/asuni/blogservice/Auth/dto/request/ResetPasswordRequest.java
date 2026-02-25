package com.asuni.blogservice.Auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank
    private String reset_password_token;

    @NotBlank
    @Size(min = 8)
    private String new_password;

    @NotBlank
    private String confirm_password;
}
