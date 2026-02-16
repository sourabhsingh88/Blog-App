package com.asuni.blogservice.service.impl ;

import com.asuni.blogservice.entity.Media;
import com.asuni.blogservice.entity.Post;
import com.asuni.blogservice.enums.MediaType;
import com.asuni.blogservice.exceptions.NotFoundException;
import com.asuni.blogservice.repository.MediaRepository;
import com.asuni.blogservice.repository.PostRepository;
import com.asuni.blogservice.service.contract.MediaService;
import com.asuni.blogservice.service.contract.S3FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final PostRepository postRepository;
    private final S3FileStorageService fileStorageService;


    @Override
    public void uploadMedia(Long postId, MultipartFile file, Long userId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        String fileUrl = fileStorageService.uploadFile(file);

        MediaType mediaType =
                file.getContentType().startsWith("video")
                        ? MediaType.VIDEO
                        : MediaType.IMAGE;

        Media media = Media.builder()
                .post(post)
                .mediaUrl(fileUrl)
                .mediaType(mediaType)
                .build();

        mediaRepository.save(media);
    }

}
