package com.asuni.blogservice.Blog.controller;

import com.asuni.blogservice.Blog.dto.request.CommentRequest;
import com.asuni.blogservice.Blog.dto.request.CreatePostRequest;
import com.asuni.blogservice.Blog.dto.request.UpdatePostRequest;
import com.asuni.blogservice.Blog.dto.response.CommentResponse;
import com.asuni.blogservice.Blog.dto.response.PostResponse;
import com.asuni.blogservice.Blog.service.contract.*;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class BlogController {

    private final PostService postService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final MediaService mediaService;
    private final TruePostService truePostService;
 private final CommentLikeService commentLikeService ;
    /* ===================== AUTH UTILS ===================== */

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

    private Long getOptionalUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        try {
            return Long.parseLong(authentication.getName());
        } catch (Exception e) {
            return null;
        }
    }

    /* ===================== CREATE ===================== */

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @Valid @ModelAttribute CreatePostRequest request,
            @RequestPart(value = "media", required = false) List<MultipartFile> media,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(postService.createPost(request, media, userId));
    }

    /* ===================== UPDATE ===================== */

    @PatchMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long postId,
            @RequestPart("request") UpdatePostRequest request,
            @RequestPart(value = "media", required = false) List<MultipartFile> media,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(
                postService.updatePost(postId, request, media, userId)
        );
    }

    /* ===================== DELETE ===================== */

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    /* ===================== READ ===================== */

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> getPost(
            @PathVariable Long postId,
            Authentication authentication
    ) {
        Long userId = getOptionalUserId(authentication);
        return ResponseEntity.ok(
                postService.getPostById(postId, userId)
        );
    }

    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts(Authentication authentication) {
        Long userId = getOptionalUserId(authentication);
        return ResponseEntity.ok(
                postService.getAllPublicPosts(userId)
        );
    }

    /* ===================== SEARCH ===================== */

    @GetMapping("/search/title")
    public ResponseEntity<List<PostResponse>> searchByTitle(
            @RequestParam String title,
            Authentication authentication
    ) {
        Long userId = getOptionalUserId(authentication);
        return ResponseEntity.ok(
                postService.searchByTitle(title, userId)
        );
    }

    @GetMapping("/search/username")
    public ResponseEntity<List<PostResponse>> searchByUsername(
            @RequestParam String username,
            Authentication authentication
    ) {
        Long userId = getOptionalUserId(authentication);
        return ResponseEntity.ok(
                postService.searchByUsername(username, userId)
        );
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
        return ResponseEntity.noContent().build();
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

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok(
                Map.of("message", "Comment deleted successfully")
        );
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
        return ResponseEntity.noContent().build();
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

    /* ===================== USER-BASED ===================== */

    @GetMapping("/user/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(
                postService.getMyPosts(userId)
        );
    }

    @GetMapping("/user/liked")
    public ResponseEntity<List<PostResponse>> getLikedPosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(
                postService.getPostsLikedByUser(userId)
        );
    }

    @GetMapping("/user/true")
    public ResponseEntity<List<PostResponse>> getTruePosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(
                postService.getTruePostsByUser(userId)
        );
    }

    @GetMapping("/user/commented")
    public ResponseEntity<List<PostResponse>> getCommentedPosts(Authentication authentication) {
        Long userId = getUserId(authentication);
        return ResponseEntity.ok(
                postService.getCommentedPostsByUser(userId)
        );
    }

//    ================= Commnets ============
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        commentLikeService.likeComment(commentId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<Void> unlikeComment(
            @PathVariable Long commentId,
            Authentication authentication
    ) {
        Long userId = getUserId(authentication);
        commentLikeService.unlikeComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                commentService.getCommentsByPostId(postId)
        );
    }

}
