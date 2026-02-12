package com.asuni.blogservice.dto.response;

import com.asuni.blogservice.enums.MediaType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponse {

    private String mediaUrl;
    private MediaType mediaType;
}
