package com.asuni.blogservice.Auth.dto.response;

import com.asuni.blogservice.Auth.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class LoginResponse {

    private String access_token;
    private String refresh_token;

    private Long id;
    private String user_name;
    private String email;
    private String full_name;
    private String phone_number;
    private Gender gender;
    private LocalDate date_of_birth;
    private String preferred_language;
    private String profile_picture_url;


}
