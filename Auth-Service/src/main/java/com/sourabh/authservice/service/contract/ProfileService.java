package com.sourabh.authservice.service.contract;

import com.sourabh.authservice.dto.request.UpdateUserRequest;
import com.sourabh.authservice.entity.User;

public interface ProfileService {

//    String updateProfile(User user, UpdateUserRequest request);
String updateProfile(Long userId, UpdateUserRequest request);

}

