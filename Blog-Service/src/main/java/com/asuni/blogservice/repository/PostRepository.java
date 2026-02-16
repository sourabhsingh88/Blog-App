package com.asuni.blogservice.repository;

import com.asuni.blogservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    long countLikesByPostId(Long postId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    long countCommentsByPostId(Long postId);


//    List<Post> findByIsDeletedFalse();
//
//    List<Post> findByUserIdAndIsDeletedFalse(Long userId);

    @Query("""
       SELECT DISTINCT p
       FROM Post p
       LEFT JOIN FETCH p.mediaList
       WHERE p.userId = :userId
       AND p.isDeleted = false
       """)
    List<Post> findByUserIdWithMedia(@Param("userId") Long userId);


//    List<Post> findByIsDeletedFalseAndTitleContainingIgnoreCase(String title);

    @Query("""
    SELECT DISTINCT p
    FROM Like l
    JOIN l.post p
    LEFT JOIN FETCH p.mediaList
    WHERE l.userId = :userId
      AND p.isDeleted = false
""")
    List<Post> findPostsLikedByUser(Long userId);


    @Query("""
       SELECT DISTINCT p
       FROM Comment c
       JOIN c.post p
       LEFT JOIN FETCH p.mediaList
       WHERE c.userId = :userId
       AND p.isDeleted = false
       """)
    List<Post> findCommentedPostsByUser(Long userId);


    @Query("""
SELECT DISTINCT p
FROM Post p
LEFT JOIN FETCH p.mediaList
WHERE p.isDeleted = false
""")
    List<Post> findAllWithMedia();


    @Query("""
       SELECT DISTINCT p
       FROM Post p
       LEFT JOIN FETCH p.mediaList
       WHERE p.isDeleted = false
       AND LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))
       """)
    List<Post> searchByTitleWithMedia(@Param("title") String title);

    @Query("""
       SELECT DISTINCT p
       FROM Post p
       LEFT JOIN FETCH p.mediaList
       WHERE p.id = :postId
       """)
    Optional<Post> findByIdWithMedia(@Param("postId") Long postId);

}
