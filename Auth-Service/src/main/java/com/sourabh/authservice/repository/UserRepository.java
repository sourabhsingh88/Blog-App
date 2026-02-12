package com.sourabh.authservice.repository;

import com.sourabh.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phoneNumber);


    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
}
