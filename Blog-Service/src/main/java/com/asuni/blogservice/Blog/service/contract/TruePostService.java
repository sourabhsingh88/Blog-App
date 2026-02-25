package com.asuni.blogservice.Blog.service.contract;



public interface TruePostService {

    void markAsTrue(Long postId, Long userId);

    void unmarkTrue(Long postId, Long userId);

}

