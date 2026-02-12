package com.asuni.blogservice.service.impl;

import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.entity.TruePost;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.repository.TruePostRepository;
import com.asuni.blogservice.service.contract.TruePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TruePostServiceImpl implements TruePostService {

    private final PostRepository postRepository;
    private final TruePostRepository postTrueRepository;

    @Override
    public void markAsTrue(Long postId, Long userId) {

        if (postTrueRepository.existsByPost_IdAndUserId(postId, userId))
            return;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        TruePost postTrue = TruePost.builder()
                .post(post)
                .userId(userId)
                .build();

        postTrueRepository.save(postTrue);
    }

    @Override
    public void unmarkTrue(Long postId, Long userId) {
        postTrueRepository.deleteByPost_IdAndUserId(postId, userId);
    }
}
