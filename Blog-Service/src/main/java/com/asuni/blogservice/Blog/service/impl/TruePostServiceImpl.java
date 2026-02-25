package com.asuni.blogservice.Blog.service.impl;

import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Blog.entity.Post;
import com.asuni.blogservice.Blog.entity.TruePost;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.Blog.repository.PostRepository;
import com.asuni.blogservice.Blog.repository.TruePostRepository;
import com.asuni.blogservice.Blog.service.contract.TruePostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TruePostServiceImpl implements TruePostService {

    private final PostRepository postRepository;
    private final TruePostRepository postTrueRepository;
    private final UserRepository userRepository;
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
    @Transactional
    @Override
    public void unmarkTrue(Long postId, Long userId) {
        postTrueRepository.deleteByPost_IdAndUserId(postId, userId);
    }
}
