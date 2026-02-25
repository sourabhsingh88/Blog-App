package com.asuni.blogservice.Blog.service.contract;

public interface CommentLikeService {

    void likeComment(Long commentId, Long userId);

    void unlikeComment(Long commentId, Long userId);
}
