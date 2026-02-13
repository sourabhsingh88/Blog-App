package com.sourabh.authservice.service.contract;

import java.util.List;

public interface UserService {

    Long getUserIdByUsername(String username);
    List<String> searchUsernames(String keyword, int page, int size, String sortBy);

}
