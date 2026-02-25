package com.asuni.blogservice.service.contract;


import com.asuni.blogservice.dto.request.CommentRequest;
import com.asuni.blogservice.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    void addComment(Long postId, Long userId, CommentRequest request);
    void deleteComment(Long commentId, Long userId);
    List<CommentResponse> getCommentsByPostId(Long postId);

}
