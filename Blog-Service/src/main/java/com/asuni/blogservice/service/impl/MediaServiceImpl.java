package com.asuni.blogservice.service.impl;


import com.asuni.blogservice.entity.Media;

import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.enums.MediaType;
import com.asuni.blogservice.repository.MediaRepository;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.service.contract.MediaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final PostRepository postRepository;

    @Override
    public void uploadMedia(Long postId, MultipartFile file, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        String fileName = file.getOriginalFilename();

        Media media = Media.builder()
                .post(post)
                .mediaUrl("/uploads/" + fileName)
                .mediaType(fileName.endsWith(".mp4") ? MediaType.VIDEO : MediaType.IMAGE)
                .build();

        mediaRepository.save(media);
    }
}
