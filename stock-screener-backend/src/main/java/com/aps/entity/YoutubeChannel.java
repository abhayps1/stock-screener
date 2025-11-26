package com.aps.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Entity
@NoArgsConstructor
public class YoutubeChannel {

    @Id
    private String channelId;   // PK

    private String handle;
    private String title;

    @OneToMany(mappedBy = "channel", cascade = CascadeType.ALL)
    private List<YoutubeVideo> videos = new ArrayList<>();
}
