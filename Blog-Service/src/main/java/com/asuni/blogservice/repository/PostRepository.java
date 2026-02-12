package com.asuni.blogservice.repository;


import com.asuni.blogservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByIsDeletedFalse();
    @Query("SELECT l.post FROM Like l WHERE l.userId = :userId")
    List<Post> findPostsLikedByUser(Long userId);

//    @Query("SELECT p FROM Post p WHERE p.userId = :userId AND p.isTrue = true")
//    List<Post> findTruePostsByUser(Long userId);

    @Query("SELECT DISTINCT c.post FROM Comment c WHERE c.userId = :userId")
    List<Post> findCommentedPostsByUser(Long userId);

}
