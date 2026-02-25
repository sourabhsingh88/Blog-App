package com.asuni.blogservice.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long userId;
    private String commentText;
    private String username;
    private LocalDateTime createdAt;

    // 🔥 ADD THIS
    private long likeCount;
}
