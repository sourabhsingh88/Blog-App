package com.sourabh.authservice.dto.response;

import com.sourabh.authservice.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class LoginResponse {

    private String accessToken;
    private String refreshToken;

    private Long id;
    private String userName;
    private String email;
    private String fullName;
    private String phoneNumber;
    private Gender gender;
    private LocalDate dateOfBirth;
}
