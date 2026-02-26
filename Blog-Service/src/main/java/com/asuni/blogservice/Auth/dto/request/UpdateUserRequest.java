package com.asuni.blogservice.Auth.dto.request;

import com.asuni.blogservice.Auth.enums.Gender;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class UpdateUserRequest {

    private String full_Name;
    private LocalDate date_of_birth;
    private Gender gender;

    private String preferred_language;

    private String email;
    private String phone_number;
}
