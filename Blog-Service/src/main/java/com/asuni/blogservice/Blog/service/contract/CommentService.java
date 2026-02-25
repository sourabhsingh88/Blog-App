package com.asuni.blogservice.Blog.service.contract;


import com.asuni.blogservice.Blog.dto.request.CommentRequest;
import com.asuni.blogservice.Blog.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void addComment(Long postId, Long userId, CommentRequest request);
    void deleteComment(Long commentId, Long userId);
    List<CommentResponse> getCommentsByPostId(Long postId);

}
