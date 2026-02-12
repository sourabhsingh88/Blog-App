package com.asuni.blogservice.service.contract;



public interface TruePostService {

    void markAsTrue(Long postId, Long userId);

    void unmarkTrue(Long postId, Long userId);

}

