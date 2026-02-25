package com.asuni.blogservice.repository;

import com.asuni.blogservice.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, Long> {

    void deleteByIdInAndPostId(List<Long> ids, Long postId);

}
