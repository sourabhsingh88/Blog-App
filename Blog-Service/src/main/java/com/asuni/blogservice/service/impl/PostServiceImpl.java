package com.asuni.blogservice.service.impl;

import com.asuni.blogservice.client.AuthClient;

import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.MediaResponse;
import com.asuni.blogservice.dto.response.PostResponse;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.exceptions.UnauthorizedException;
import com.asuni.blogservice.repository.MediaRepository;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.repository.TruePostRepository;
import com.asuni.blogservice.service.contract.MediaService;
import com.asuni.blogservice.service.contract.PostService;
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
    private final AuthClient authFeignClient;




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

        if (post.isDeleted()) {
            throw new NotFoundException("Post not found");
        }

        if (!post.getUserId().equals(userId)) {
            throw new UnauthorizedException("You are not allowed to update this post");
        }

        // TEXT UPDATE
        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            post.setDescription(request.getDescription());
        }

        if (request.getPriority() != null) {
            post.setPriority(request.getPriority());
        }

        // REMOVE MEDIA
        if (request.getRemoveMediaIds() != null && !request.getRemoveMediaIds().isEmpty()) {
            mediaRepository.deleteByIdInAndPostId(
                    request.getRemoveMediaIds(),
                    postId
            );
        }

        // ADD NEW MEDIA
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
            throw new UnauthorizedException("You are not allowed to delete this post");
        }

        post.setDeleted(true);
        postRepository.save(post);
    }

    /* ===================== GET ===================== */

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(Long postId) {
        Post post = postRepository.findByIdWithMedia(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));
        return mapToResponse(post);
    }



    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllWithMedia()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getTruePostsByUser(Long userId) {
        return truePostRepository.findPostsMarkedTrueByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }



    /* ===================== SEARCH ===================== */

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> searchByTitle(String title) {
        return postRepository.searchByTitleWithMedia(title)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }




    @Override
    @Transactional(readOnly = true)
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
                            .findByUserIdWithMedia(userId)
                            .stream();
                })
                .map(this::mapToResponse)
                .toList();
    }


    /* ===================== USER BASED ===================== */

    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getPostsLikedByUser(Long userId) {
        return postRepository.findPostsLikedByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }




    @Override
    @Transactional(readOnly = true)
    public List<PostResponse> getCommentedPostsByUser(Long userId) {
        return postRepository.findCommentedPostsByUser(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    /* ===================== MAPPER ===================== */

    private PostResponse mapToResponse(Post post) {

        List<MediaResponse> mediaResponses =
                post.getMediaList() != null
                        ? post.getMediaList().stream()
                        .map(m -> new MediaResponse(
                                m.getId(),
                                m.getMediaUrl(),
                                m.getMediaType()
                        ))
                        .toList()
                        : List.of();

        return PostResponse.builder()
                .id(post.getId())
                .userId(post.getUserId())
                .title(post.getTitle())
                .description(post.getDescription())
                .priority(post.getPriority())
                .createdAt(post.getCreatedAt())
                .likeCount((int) postRepository.countLikesByPostId(post.getId()))
                .commentCount((int) postRepository.countCommentsByPostId(post.getId()))
                .media(mediaResponses)
                .build();
    }

}
