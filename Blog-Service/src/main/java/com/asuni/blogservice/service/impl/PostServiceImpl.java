package com.asuni.blogservice.service.impl;


import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.repository.TruePostRepository;
import com.asuni.blogservice.service.contract.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private  final TruePostRepository truePostRepository ;



    @Override
    public PostResponse createPost(CreatePostRequest request, Long userId) {

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .build();

        Post saved = postRepository.save(post);

        return mapToResponse(saved);
    }

    @Override
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId))
            throw new RuntimeException("Unauthorized");

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setPriority(request.getPriority());

        return mapToResponse(postRepository.save(post));
    }

    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!post.getUserId().equals(userId))
            throw new RuntimeException("Unauthorized");

        post.setDeleted(true);
        postRepository.save(post);
    }

    @Override
    public PostResponse getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        return mapToResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .description(post.getDescription())
                .priority(post.getPriority())
                .createdAt(post.getCreatedAt())
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .build();
    }
    @Override
    public List<PostResponse> getPostsLikedByUser(Long userId) {
        return postRepository.findPostsLikedByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PostResponse> getTruePostsByUser(Long userId) {
        return truePostRepository.findPostsMarkedTrueByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    public List<PostResponse> getCommentedPostsByUser(Long userId) {
        return postRepository.findCommentedPostsByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

}
