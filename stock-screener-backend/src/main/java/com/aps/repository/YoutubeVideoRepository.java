package com.aps.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.aps.entity.YoutubeVideo;

public interface YoutubeVideoRepository extends JpaRepository<YoutubeVideo, String> {

    List<YoutubeVideo> findByChannel_ChannelId(String channelId);
    
}
