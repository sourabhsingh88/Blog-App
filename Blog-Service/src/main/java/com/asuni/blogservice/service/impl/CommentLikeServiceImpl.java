package com.asuni.blogservice.service.impl;

import com.asuni.blogservice.entity.Comment;
import com.asuni.blogservice.entity.CommentLike;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.repository.CommentLikeRepository;
import com.asuni.blogservice.repository.CommentRepository;
import com.asuni.blogservice.service.contract.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentLikeServiceImpl implements CommentLikeService {

    private final CommentLikeRepository commentLikeRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public void likeComment(Long commentId, Long userId) {

        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            return; // already liked
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        CommentLike like = CommentLike.builder()
                .comment(comment)
                .userId(userId)
                .build();

        commentLikeRepository.save(like);
    }

    @Override
    @Transactional
    public void unlikeComment(Long commentId, Long userId) {

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }
}

