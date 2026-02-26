package com.asuni.blogservice.Auth.dto.response;

import com.asuni.blogservice.Auth.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserResponse {

    private Long id;
    private String user_name;
    private String email;
    private String full_name;
    private String phone_number;
    private Gender gender;
    private LocalDate date_of_birth;
    private String preferred_language;
    private String profile_picture_url;
    private boolean email_verified;
    private boolean phone_number_verified;
}
