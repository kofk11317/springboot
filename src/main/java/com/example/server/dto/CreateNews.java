package com.example.server.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CreateNews {
    private int createNewsNum;
    private int clusterNum;
    private String category;
    private String title;
    private LocalDateTime createNewsDate;
    private String description;
    private int joind;
    private int likeCount;
    private int dislikeCount;
    private LocalDateTime pubDate;
    private String keyword;
    private String literaryStyle;
    private String thumbnailPrompt;
}
