package com.aps.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.aps.entity.YoutubeChannel;
import com.aps.entity.YoutubeVideo;
import com.aps.service.YoutubeService;

@RestController
public class YoutubeController {

    @Autowired
    private YoutubeService youtubeService;

    @GetMapping("/get-channel-id")
    public YoutubeChannel getChannelId(@RequestParam String handle) {
        return youtubeService.getChannelId(handle);
    }

    @GetMapping("/get-channel-videos")
    public List<YoutubeVideo> getChannelVideos(@RequestParam String channelId) {
        return youtubeService.getVideosByChannelId(channelId);
    }
}

