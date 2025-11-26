package com.aps.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aps.entity.YoutubeVideo;

public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, String> {
    
}
