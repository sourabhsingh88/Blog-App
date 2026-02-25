package com.asuni.blogservice.Blog.dto.request;



import com.asuni.blogservice.Blog.enums.Priority;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdatePostRequest {

    @Size(max = 200)
    private String title;

    private String description;

    private Boolean hideUsername;

    private Priority priority;
    private List<Long> remove_media_ids;
    private List<MultipartFile> new_media;

}
