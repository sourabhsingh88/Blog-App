package com.asuni.blogservice.service.contract;

import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface PostService {

    PostResponse createPost(
            CreatePostRequest request,
            List<MultipartFile> mediaFiles,
            Long userId
    );

    PostResponse updatePost(
            Long postId,
            UpdatePostRequest request,
            List<MultipartFile> mediaFiles,
            Long userId
    );

    void deletePost(Long postId, Long userId);

    PostResponse getPostById(Long postId, Long currentUserId);

    List<PostResponse> getAllPublicPosts(Long currentUserId);

    List<PostResponse> searchByTitle(String title, Long currentUserId);

    List<PostResponse> searchByUsername(String username, Long currentUserId);

    List<PostResponse> getMyPosts(Long userId);

    List<PostResponse> getPostsLikedByUser(Long userId);

    List<PostResponse> getCommentedPostsByUser(Long userId);

    List<PostResponse> getTruePostsByUser(Long userId);
}
