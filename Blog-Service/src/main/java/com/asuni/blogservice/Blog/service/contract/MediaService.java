package com.asuni.blogservice.Blog.service.contract;


import org.springframework.web.multipart.MultipartFile;

public interface MediaService {

    void uploadMedia(Long postId, MultipartFile file, Long userId);
}
