package com.sourabh.authservice.dto.request;


import com.sourabh.authservice.enums.Gender;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String fullName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private MultipartFile aadhaarImage;

    // sensitive
    private String email;
    private String phoneNumber;
}
