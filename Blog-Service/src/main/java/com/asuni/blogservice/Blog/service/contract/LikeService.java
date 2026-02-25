package com.asuni.blogservice.service.contract;


public interface LikeService {

    void likePost(Long postId, Long userId);

    void unlikePost(Long postId, Long userId);
}
