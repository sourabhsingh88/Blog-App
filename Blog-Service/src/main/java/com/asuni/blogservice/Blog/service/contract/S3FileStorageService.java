package com.asuni.blogservice.Blog.service.contract;


import org.springframework.web.multipart.MultipartFile;

public interface S3FileStorageService {
    String uploadFile(MultipartFile file);
}
