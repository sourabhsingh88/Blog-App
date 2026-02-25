package com.asuni.blogservice.Auth.service.contract;

import com.asuni.blogservice.Auth.dto.request.UpdateUserRequest;

public interface ProfileService {

//    String updateProfile(User user, UpdateUserRequest request);
String updateProfile(Long userId, UpdateUserRequest request);

}

