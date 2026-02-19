package com.asuni.blogservice.repository;


import com.asuni.blogservice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    int deleteByIdAndUserId(Long id, Long userId);

    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);


}
