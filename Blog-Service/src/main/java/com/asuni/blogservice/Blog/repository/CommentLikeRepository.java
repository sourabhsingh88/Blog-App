package com.asuni.blogservice.repository;

import com.asuni.blogservice.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentIdAndUserId(Long commentId, Long userId);

    void deleteByCommentIdAndUserId(Long commentId, Long userId);

    long countByCommentId(Long commentId);
}

