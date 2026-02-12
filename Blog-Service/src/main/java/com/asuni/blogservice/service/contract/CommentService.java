package com.asuni.blogservice.service.contract;


import com.asuni.blogservice.dto.request.CommentRequest;

public interface CommentService {

    void addComment(Long postId, Long userId, CommentRequest request);
}
