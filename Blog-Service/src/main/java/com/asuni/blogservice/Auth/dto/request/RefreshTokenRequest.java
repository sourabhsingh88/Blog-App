package com.asuni.blogservice.Auth.dto.request;

import lombok.Data;

@Data
public class RefreshTokenRequest {
    private String refresh_token;
}
