package com.asuni.blogservice.Blog.repository;


import com.asuni.blogservice.Blog.entity.Post;
import com.asuni.blogservice.Blog.entity.TruePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TruePostRepository extends JpaRepository<TruePost, Long> {

    boolean existsByPost_IdAndUserId(Long postId, Long userId);

    void deleteByPost_IdAndUserId(Long postId, Long userId);

    @Query("""
    SELECT DISTINCT p
    FROM TruePost t
    JOIN t.post p
    LEFT JOIN FETCH p.mediaList
    WHERE t.userId = :userId
      AND p.isDeleted = false
""")
    List<Post> findPostsMarkedTrueByUser(Long userId);



}



