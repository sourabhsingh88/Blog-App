package com.asuni.blogservice.Auth.dto.response;

import com.asuni.blogservice.Auth.enums.Gender;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;


@Data
@Builder
public class PhoneLoginResponse {
    private String phoneLoginToken;
    private String phoneOtp;

}
