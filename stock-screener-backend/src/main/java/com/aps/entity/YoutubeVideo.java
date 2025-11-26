package com.aps.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class YoutubeVideo {

    @Id
    private String videoId;   // PK

    private String title;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @JsonIgnore 
    private YoutubeChannel channel;   // FK to YoutubeChannel
}


