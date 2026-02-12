package com.asuni.blogservice.service.impl;


import com.asuni.blogservice.entity.Like;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.repository.LikeRepository;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.service.contract.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;

    @Override
    public void likePost(Long postId, Long userId) {

        if (likeRepository.existsByPostIdAndUserId(postId, userId))
            return;

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Like like = Like.builder()
                .post(post)
                .userId(userId)
                .build();

        likeRepository.save(like);
    }

    @Override
    public void unlikePost(Long postId, Long userId) {
        likeRepository.deleteByPostIdAndUserId(postId, userId);
    }
}
