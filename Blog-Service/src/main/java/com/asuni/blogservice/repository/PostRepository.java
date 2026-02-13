package com.asuni.blogservice.repository;

import com.asuni.blogservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    long countLikesByPostId(Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsByPostId(Long postId);


    List<Post> findByIsDeletedFalse();

    List<Post> findByUserIdAndIsDeletedFalse(Long userId);

    List<Post> findByIsDeletedFalseAndTitleContainingIgnoreCase(String title);

    @Query("SELECT l.post FROM Like l WHERE l.userId = :userId")
    List<Post> findPostsLikedByUser(Long userId);

    @Query("SELECT DISTINCT c.post FROM Comment c WHERE c.userId = :userId")
    List<Post> findCommentedPostsByUser(Long userId);
}
