package com.asuni.blogservice.service.contract;


import com.asuni.blogservice.dto.request.CreatePostRequest;
import com.asuni.blogservice.dto.request.UpdatePostRequest;
import com.asuni.blogservice.dto.response.PostResponse;

import java.util.List;

public interface PostService {

    PostResponse createPost(CreatePostRequest request, Long userId);

    PostResponse updatePost(Long postId, UpdatePostRequest request, Long userId);

    void deletePost(Long postId, Long userId);

    PostResponse getPostById(Long postId);

    List<PostResponse> getAllPosts();
    List<PostResponse> getPostsLikedByUser(Long userId);

    List<PostResponse> getTruePostsByUser(Long userId);

    List<PostResponse> getCommentedPostsByUser(Long userId);

    List<PostResponse> searchByTitle(String title);

    List<PostResponse> searchByUsername(String username);


}
