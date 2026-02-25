package com.asuni.blogservice.Blog.service.impl;



import com.asuni.blogservice.Auth.repository.UserRepository;
import com.asuni.blogservice.Blog.dto.request.CommentRequest;
import com.asuni.blogservice.Blog.dto.response.CommentResponse;
import com.asuni.blogservice.Blog.entity.Comment;
import com.asuni.blogservice.Blog.entity.Post;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.Blog.repository.CommentLikeRepository;
import com.asuni.blogservice.Blog.repository.CommentRepository;
import com.asuni.blogservice.Blog.repository.PostRepository;

import com.asuni.blogservice.Blog.service.contract.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final UserRepository userRepository;

    @Override
    public void addComment(Long postId, Long userId, CommentRequest request) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = Comment.builder()
                .post(post)
                .userId(userId)
                .commentText(request.getComment_text())
                .build();

        commentRepository.save(comment);
    }
    @Transactional
    public void deleteComment(Long commentId, Long userId) {

        int deleted = commentRepository.deleteByIdAndUserId(commentId, userId);

        if (deleted == 0) {
            throw new NotFoundException("Comment not found or not owned by user");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentResponse> getCommentsByPostId(Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> {

                    long likeCount =
                            commentLikeRepository.countByCommentId(comment.getId()); // 🔥 STEP 3

                    String username = userRepository.findById(comment.getUserId())
                            .map(user -> user.getUsername())
                            .orElse("Unknown");

                    return CommentResponse.builder()
                            .id(comment.getId())
                            .userId(comment.getUserId())
                            .username(username)
                            .commentText(comment.getCommentText())
                            .createdAt(comment.getCreatedAt())
                            .likeCount(likeCount)
                            .build();
                })
                .toList();
    }
}
