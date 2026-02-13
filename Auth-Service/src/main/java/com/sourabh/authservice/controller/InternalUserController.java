package com.sourabh.authservice.controller;

import com.sourabh.authservice.service.contract.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    /* ===================== GET USER ID ===================== */

    @GetMapping("/username/{username}")
    public ResponseEntity<Long> getUserIdByUsername(@PathVariable String username) {

        if (!StringUtils.hasText(username)) {
            return ResponseEntity.badRequest().build();
        }

        Long userId = userService.getUserIdByUsername(username);
        return ResponseEntity.ok(userId);
    }

    /* ===================== SEARCH USERS ===================== */

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "username") String sortBy
    ) {

        if (!StringUtils.hasText(keyword)) {
            return ResponseEntity.badRequest().build();
        }

        List<String> users = userService.searchUsernames(keyword, page, size, sortBy);

        return ResponseEntity.ok(users);
    }
}
