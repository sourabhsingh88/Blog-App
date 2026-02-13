package com.sourabh.authservice.service.impl;

import com.sourabh.authservice.entity.User;
import com.sourabh.authservice.exceptions.NotFoundException;
import com.sourabh.authservice.repository.UserRepository;
import com.sourabh.authservice.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public Long getUserIdByUsername(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));

        return user.getId();
    }


    @Override
    public List<String> searchUsernames(String keyword, int page, int size, String sortBy) {

        Sort sort;

        if ("time".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "createdAt"); // latest users first
        } else {
            sort = Sort.by(Sort.Direction.ASC, "username"); // default alphabetical
        }

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<User> result = userRepository
                .findByUsernameContainingIgnoreCase(keyword, pageable);

        return result.stream()
                .map(User::getUsername)
                .toList();
    }

}
