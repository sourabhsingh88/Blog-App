package com.asuni.blogservice.repository;


import com.asuni.blogservice.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaRepository extends JpaRepository<Media, Long> {
}
