package com.asuni.blogservice.Auth.service.contract;

import java.util.List;

public interface UserService {

    Long getUserIdByUsername(String username);
    List<String> searchUsernames(String keyword, int page, int size, String sortBy);
    String getUsernameByUserId(Long id);
}
