package com.asuni.blogservice.Blog.dto.response;
import com.asuni.blogservice.Blog.enums.Priority;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {

    private Long id;
    private Long userId;
    private String username;
    private String title;
    private String description;
    private Priority priority;
    private boolean is_true;
    private LocalDateTime createdAt;

    private long like_count;
    private long comment_count;


    private List<MediaResponse> media;

}
