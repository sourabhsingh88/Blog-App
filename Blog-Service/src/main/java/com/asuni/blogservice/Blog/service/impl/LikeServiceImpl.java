package com.asuni.blogservice.service.impl;


import com.asuni.blogservice.entity.Like;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.repository.LikeRepository;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.service.contract.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;

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
    @PostMapping
    @Transactional
    public void unlikePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (post.isDeleted()) {
            throw new NotFoundException("Post not found");
        }

        int deleted = likeRepository.deleteByPostIdAndUserId(postId, userId);

        if (deleted == 0) {
            throw new NotFoundException("Like not found");
        }
    }

}
