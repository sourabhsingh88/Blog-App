package com.asuni.blogservice.Auth.service.contract;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String uploadFile(MultipartFile file, String folder);
    void deleteFile(String url);

}
