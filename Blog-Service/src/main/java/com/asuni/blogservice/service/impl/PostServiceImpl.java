package com.asuni.blogservice.service.impl;

import com.asuni.blogservice.client.AuthClient;

import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.exceptions.UnauthorizedException;
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
    private final TruePostRepository truePostRepository;
    private final AuthClient authFeignClient;

    /* ===================== CREATE ===================== */

    @Override
    public PostResponse createPost(CreatePostRequest request, Long userId) {

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .build();

        return mapToResponse(postRepository.save(post));
    }

    /* ===================== UPDATE ===================== */

    @Override
    public PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        post.setTitle(request.getTitle());
        post.setDescription(request.getDescription());
        post.setPriority(request.getPriority());

        return mapToResponse(postRepository.save(post));
    }

    /* ===================== DELETE ===================== */

    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to delete this post");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    /* ===================== GET ===================== */

    @Override
    public PostResponse getPostById(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        return mapToResponse(post);
    }

    @Override
    public List<PostResponse> getAllPosts() {
        return postRepository.findByIsDeletedFalse()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== SEARCH ===================== */

    @Override
    public List<PostResponse> searchByTitle(String title) {
        return postRepository
                .findByIsDeletedFalseAndTitleContainingIgnoreCase(title)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    @Override
    public List<PostResponse> searchByUsername(String username) {

        List<String> usernames = authFeignClient.searchUsers(
                username, 0, 20, "username"
        );

        if (usernames.isEmpty()) {
            return List.of();
        }

        return usernames.stream()
                .flatMap(name -> {
                    Long userId = authFeignClient.getUserIdByUsername(name);
                    return postRepository
                            .findByUserIdAndIsDeletedFalse(userId)
                            .stream();
                })
                .map(this::mapToResponse)
                .toList();
    }




    /* ===================== USER BASED ===================== */

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

    /* ===================== MAPPER ===================== */

    private PostResponse mapToResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .description(post.getDescription())
                .priority(post.getPriority())
                .createdAt(post.getCreatedAt())
                .likeCount((int) postRepository.countLikesByPostId(post.getId()))
                .commentCount((int) postRepository.countCommentsByPostId(post.getId()))
                .build();
    }
}
