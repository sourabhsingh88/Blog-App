package com.asuni.blogservice.Blog.service.contract;


public interface LikeService {

    void likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);
}
