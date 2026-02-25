package com.asuni.blogservice.Blog.service.impl;


import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Blog.dto.request.CreatePostRequest;
import com.asuni.blogservice.Blog.dto.request.UpdatePostRequest;
import com.asuni.blogservice.Blog.dto.response.MediaResponse;
import com.asuni.blogservice.Blog.dto.response.PostResponse;
import com.asuni.blogservice.Blog.entity.Post;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import com.asuni.blogservice.Blog.repository.MediaRepository;
import com.asuni.blogservice.Blog.repository.PostRepository;
import com.asuni.blogservice.Blog.repository.TruePostRepository;
import com.asuni.blogservice.Blog.service.contract.MediaService;
import com.asuni.blogservice.Blog.service.contract.PostService;
import com.asuni.blogservice.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final TruePostRepository truePostRepository;
    private final MediaRepository mediaRepository;
    private final MediaService mediaService;
    private final UserRepository userRepository;


    /* ===================== CREATE ===================== */

    @Override
    @Transactional
    public PostResponse createPost(
            CreatePostRequest request,
            List<MultipartFile> mediaFiles,
            Long userId
    ) {

        Post post = Post.builder()
                .userId(userId)
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .isUsernameHidden(Boolean.TRUE.equals(request.getHideUsername()))
                .build();

        post = postRepository.save(post);

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                mediaService.uploadMedia(post.getId(), file, userId);
            }
        }

        return mapToResponse(post);
    }

    /* ===================== UPDATE ===================== */

    @Override
    @Transactional
    public PostResponse updatePost(
            Long postId,
            UpdatePostRequest request,
            List<MultipartFile> mediaFiles,
            Long userId
    ) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("Not allowed");
        }

        if (request.getTitle() != null) post.setTitle(request.getTitle());
        if (request.getDescription() != null) post.setDescription(request.getDescription());
        if (request.getPriority() != null) post.setPriority(request.getPriority());

        if (request.getHideUsername() != null) {
            post.setUsernameHidden(request.getHideUsername());
        }

        if (request.getRemove_media_ids() != null && !request.getRemove_media_ids().isEmpty()) {
            mediaRepository.deleteByIdInAndPostId(
                    request.getRemove_media_ids(),
                    postId
            );
        }

        if (mediaFiles != null && !mediaFiles.isEmpty()) {
            for (MultipartFile file : mediaFiles) {
                mediaService.uploadMedia(postId, file, userId);
            }
        }

        postRepository.save(post);
        return mapToResponse(post);
    }

    /* ===================== DELETE ===================== */

    @Override
    public void deletePost(Long postId, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("Not allowed");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    /* ===================== READ ===================== */

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId, Long currentUserId) {

        Post post = postRepository.findByIdWithMedia(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // ❌ NO BLOCKING, post is always public
        return mapToResponse(post);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPublicPosts(Long currentUserId) {

        return postRepository.findAllWithMedia()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== SEARCH ===================== */

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> searchByTitle(String title, Long currentUserId) {

        return postRepository.searchByTitleWithMedia(title)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * 🔥 IMPORTANT:
     * Hide-username wali posts yahan nahi aani chahiye
     */
    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> searchByUsername(String username, Long currentUserId) {

        return userRepository.findByUsernameContainingIgnoreCase(username)
                .stream()
                .flatMap(user ->
                        postRepository
                                .findByUserIdAndUsernameVisible(user.getId())
                                .stream()
                )
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== USER BASED ===================== */

    @Override
    public List<PostResponse> getMyPosts(Long userId) {

        return postRepository.findByUserIdWithMedia(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<PostResponse> getPostsLikedByUser(Long userId) {

        return postRepository.findPostsLikedByUser(userId)
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

    @Override
    public List<PostResponse> getTruePostsByUser(Long userId) {

        return truePostRepository.findPostsMarkedTrueByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    /* ===================== MAPPER ===================== */

    private PostResponse mapToResponse(Post post) {

        String username = userRepository.findById(post.getUserId())
                .map(user -> user.getUsername())
                .orElse("Unknown");

        // 🔥 ONLY MASK NAME, POST IS STILL PUBLIC
        if (post.isUsernameHidden()) {
            username = "username_hidden";
        }

        List<MediaResponse> media =
                post.getMediaList() == null
                        ? List.of()
                        : post.getMediaList().stream()
                        .map(m -> new MediaResponse(
                                m.getId(),
                                m.getMediaUrl(),
                                m.getMediaType()
                        ))
                        .toList();

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .username(username)
                .title(post.getTitle())
                .description(post.getDescription())
                .priority(post.getPriority())
                .createdAt(post.getCreatedAt())
                .like_count(postRepository.countLikesByPostId(post.getId()))
                .comment_count(postRepository.countCommentsByPostId(post.getId()))
                .media(media)
                .build();
    }
}
