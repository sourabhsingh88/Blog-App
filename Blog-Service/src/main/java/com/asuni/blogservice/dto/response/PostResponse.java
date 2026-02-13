package com.asuni.blogservice.dto.response ;
import com.asuni.blogservice.enums.Priority;
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
    private boolean isTrue;
    private LocalDateTime createdAt;

    private long likeCount;
    private long commentCount;


    private List<MediaResponse> media;
}
