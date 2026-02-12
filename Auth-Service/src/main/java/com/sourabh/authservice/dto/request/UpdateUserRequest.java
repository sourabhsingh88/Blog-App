package com.sourabh.authservice.dto.request;


import com.sourabh.authservice.enums.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;

    // sensitive
    private String email;
    private String phoneNumber;
}
