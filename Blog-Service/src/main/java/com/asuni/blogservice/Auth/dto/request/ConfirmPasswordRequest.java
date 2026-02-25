package com.asuni.blogservice.Auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPasswordRequest {

    @NotBlank
    private String password;
}
