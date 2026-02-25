package com.asuni.blogservice.Blog.dto.response;

import com.asuni.blogservice.Blog.enums.MediaType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaResponse {


        private Long id;
        private String media_url;
        private MediaType media_type;
    }


