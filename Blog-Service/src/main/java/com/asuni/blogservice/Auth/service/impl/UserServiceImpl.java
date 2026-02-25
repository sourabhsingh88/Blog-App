package com.asuni.blogservice.Auth.service.impl;

import com.asuni.blogservice.Auth.entity.User;
import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Auth.service.contract.UserService;
import com.asuni.blogservice.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

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
    @Override
    public String getUsernameByUserId(Long id) {
        return userRepository.findById(id)
                .map(User::getUsername)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
