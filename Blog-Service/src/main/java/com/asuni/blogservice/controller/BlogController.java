package com.asuni.blogservice.controller;

import com.asuni.blogservice.dto.request.CommentRequest;
import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;
import com.asuni.blogservice.service.contract.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    /* ===================== POST CRUD ===================== */

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.createPost(request, userId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @Valid @RequestBody UpdatePostRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.updatePost(postId, request, userId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<String> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        postService.deletePost(postId, userId);
        return ResponseEntity.ok("Post deleted successfully");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPostById(postId));
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    /* ===================== LIKE ===================== */

    @PostMapping("/{postId}/like")
    public ResponseEntity<String> likePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        likeService.likePost(postId, userId);
        return ResponseEntity.ok("Post liked");
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<String> unlikePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        likeService.unlikePost(postId, userId);
        return ResponseEntity.ok("Post unliked");
    }

    /* ===================== COMMENT ===================== */

    @PostMapping("/{postId}/comments")
    public ResponseEntity<String> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        commentService.addComment(postId, userId, request);
        return ResponseEntity.ok("Comment added");
    }

    /* ===================== MEDIA ===================== */

    @PostMapping("/{postId}/media")
    public ResponseEntity<String> uploadMedia(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        mediaService.uploadMedia(postId, file, userId);
        return ResponseEntity.ok("Media uploaded");
    }

    /* ===================== TRUE ===================== */

    @PatchMapping("/{postId}/true")
    public ResponseEntity<String> markAsTrue(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        truePostService.markAsTrue(postId, userId);
        return ResponseEntity.ok("Post marked as true");
    }

    @DeleteMapping("/{postId}/true")
    public ResponseEntity<String> unmarkTrue(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = Long.parseLong(authentication.getName());
        truePostService.unmarkTrue(postId, userId);
        return ResponseEntity.ok("Post unmarked");
    }

    /* ===================== USER BASED ===================== */

    @GetMapping("/user/liked")
    public ResponseEntity<List<PostResponse>> getLikedPosts(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.getPostsLikedByUser(userId));
    }

    @GetMapping("/user/true")
    public ResponseEntity<List<PostResponse>> getTruePosts(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.getTruePostsByUser(userId));
    }

    @GetMapping("/user/commented")
    public ResponseEntity<List<PostResponse>> getCommentedPosts(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok(postService.getCommentedPostsByUser(userId));
    }
}
