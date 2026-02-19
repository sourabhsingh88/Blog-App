package com.asuni.blogservice.dto.response;

import com.asuni.blogservice.enums.MediaType;
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


