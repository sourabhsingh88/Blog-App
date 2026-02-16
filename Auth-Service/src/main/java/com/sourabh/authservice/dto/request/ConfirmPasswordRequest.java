package com.sourabh.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmPasswordRequest {

    @NotBlank
    private String password;
}
