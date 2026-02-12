package com.sourabh.authservice.dto.response;

import com.sourabh.authservice.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class LoginResponse {

    private String token;
    private Long id;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
}
