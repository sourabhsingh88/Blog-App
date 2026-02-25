package com.asuni.blogservice.Auth.dto.response;

import lombok.Getter;

@Getter
public class AuthResponse {

    public String token ;

    public AuthResponse(String token) {
        this.token = token;
    }
    public String getToken() {
        return token ;
    }
}
