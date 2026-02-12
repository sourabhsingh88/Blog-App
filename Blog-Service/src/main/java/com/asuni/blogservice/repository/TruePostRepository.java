package com.asuni.blogservice.repository;


import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.entity.TruePost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TruePostRepository extends JpaRepository<TruePost, Long> {

    boolean existsByPost_IdAndUserId(Long postId, Long userId);

    void deleteByPost_IdAndUserId(Long postId, Long userId);

    @Query("SELECT t.post FROM TruePost t WHERE t.userId = :userId")
    List<Post> findPostsMarkedTrueByUser(Long userId);


}



