package com.sourabh.authservice.dto.request;

import com.sourabh.authservice.enums.Gender;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String fullName;
    private LocalDate date_of_birth;
    private Gender gender;

    private String preferred_language;

    private MultipartFile aadhaar_image;
    private MultipartFile profile_picture;

    // sensitive
    private String email;
    private String phone_number;
}
