package com.asuni.blogservice.controller;

import com.asuni.blogservice.dto.request.CommentRequest;
import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import com.asuni.blogservice.service.contract.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BlogController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final MediaService mediaService;
    private final TruePostService truePostService;

    private Long getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("Authentication required");
        }

        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid authentication token");
        }
    }

    /* ===================== POST CRUD ===================== */

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(request, userId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(postService.updatePost(postId, request, userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();   // 204
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /* ===================== SEARCH ===================== */

    @GetMapping("/search/title")
    public ResponseEntity<List<PostResponse>> searchByTitle(
            @RequestParam String title
    ) {
        return ResponseEntity.ok(postService.searchByTitle(title));
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<PostResponse>> searchByUsername(
            @RequestParam String username
    ) {
        return ResponseEntity.ok(postService.searchByUsername(username));
    }

    /* ===================== LIKE ===================== */

    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        likeService.likePost(postId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        likeService.unlikePost(postId, userId);
        return ResponseEntity.noContent().build();   // 204
    }

    /* ===================== COMMENT ===================== */

    @PostMapping("/{postId}/comments")
    public ResponseEntity<Void> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        commentService.addComment(postId, userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* ===================== MEDIA ===================== */

    @PostMapping("/{postId}/media")
    public ResponseEntity<Void> uploadMedia(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        mediaService.uploadMedia(postId, file, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /* ===================== TRUE ===================== */

    @PatchMapping("/{postId}/true")
    public ResponseEntity<Void> markAsTrue(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        truePostService.markAsTrue(postId, userId);
        return ResponseEntity.noContent().build(); // better
    }

    @DeleteMapping("/{postId}/true")
    public ResponseEntity<Void> unmarkTrue(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        truePostService.unmarkTrue(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /* ===================== USER BASED ===================== */

    @GetMapping("/user/liked")
    public ResponseEntity<List<PostResponse>> getLikedPosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(postService.getPostsLikedByUser(userId));
    }

    @GetMapping("/user/true")
    public ResponseEntity<List<PostResponse>> getTruePosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(postService.getTruePostsByUser(userId));
    }

    @GetMapping("/user/commented")
    public ResponseEntity<List<PostResponse>> getCommentedPosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(postService.getCommentedPostsByUser(userId));
    }
}
