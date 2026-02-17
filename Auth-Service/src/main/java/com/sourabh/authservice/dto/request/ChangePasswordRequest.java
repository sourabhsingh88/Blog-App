package com.sourabh.authservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {
    @NotBlank
    private String old_password;

    @NotBlank
    @Size(min = 8)
    private String new_password;

    @NotBlank
    private String confirm_password;

}
