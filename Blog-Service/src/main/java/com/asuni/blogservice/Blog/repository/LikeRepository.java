package com.asuni.blogservice.Blog.repository;

import com.asuni.blogservice.Blog.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    @Modifying
    @Transactional
    int deleteByPostIdAndUserId(Long postId, Long userId);
}
