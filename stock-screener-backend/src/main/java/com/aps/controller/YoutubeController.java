package com.aps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aps.entity.YoutubeChannel;
import com.aps.entity.YoutubeVideo;
import com.aps.service.YoutubeService;

@RestController
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    @GetMapping("/fetch-channel-id")
    public YoutubeChannel fetchChannelId(@RequestParam String handle) {
        return youtubeService.fetchChannelId(handle);
    }

    @GetMapping("/fetch-channel-videos")
    public List<YoutubeVideo> fetchChannelVideos(@RequestParam String channelId) {
        return youtubeService.fetchVideosByChannelId(channelId);
    }

    @GetMapping("/channel/{id}/videos")
    public ResponseEntity<List<YoutubeVideo>> getChannelVideos(@PathVariable String id) {
        return ResponseEntity.ok(youtubeService.getChannelVideos(id));
    }
}

