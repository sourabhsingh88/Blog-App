package com.asuni.blogservice.service.impl;


import com.asuni.blogservice.dto.request.CommentRequest;
import com.asuni.blogservice.entity.Comment;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.repository.CommentRepository;
import com.asuni.blogservice.repository.PostRepository;

import com.asuni.blogservice.service.contract.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public void addComment(Long postId, Long userId, CommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .userId(userId)
                .commentText(request.getCommentText())
                .build();

        commentRepository.save(comment);
    }
}
