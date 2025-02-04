package com.projects.thinkify.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class Post {

    private String postId;

    private String postContent;

    private String postUploadDateTime;

    private long postUploadTimestamp;

    private String postedBy;

    private int likes = 0;

    private int dislikes = 0;


    public Post(String postId, String content, String postedBy, String postUploadDateTime, long postUploadTimestamp) {
        this.postId = postId;
        this.postContent = content;
        this.postedBy = postedBy;
        this.postUploadDateTime = postUploadDateTime;
        this.postUploadTimestamp = postUploadTimestamp;
    }

}
