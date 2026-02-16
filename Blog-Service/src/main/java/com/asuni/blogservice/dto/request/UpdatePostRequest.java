package com.asuni.blogservice.dto.request;



import com.asuni.blogservice.enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    private Priority priority;
    private List<Long> removeMediaIds;
    private List<MultipartFile> newMedia;

}
