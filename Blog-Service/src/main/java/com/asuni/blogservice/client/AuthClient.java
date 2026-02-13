package com.asuni.blogservice.client;

import com.asuni.blogservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(
        name = "auth-service",
        url = "http://localhost:8081",
        configuration = FeignConfig.class
)
public interface AuthClient {

    @GetMapping("/internal/users/username/{username}")
    Long getUserIdByUsername(@PathVariable String username);

    @GetMapping("/internal/users/search")
    List<String> searchUsers(
            @RequestParam String keyword,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy
    );
}
