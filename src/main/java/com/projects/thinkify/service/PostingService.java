package com.projects.thinkify.service;

import com.projects.thinkify.controller.DriverController;
import com.projects.thinkify.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostingService extends Post{
    Logger logger = LoggerFactory.getLogger(PostingService.class);

    List<Post> allPosts = new ArrayList<>();

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    Post post;

    DriverController driver;

    Map<Integer, List<Post>> postsByUser = new HashMap<>();


    /**
     * A method which posts a content in the account of userId, and maintains the date, time and timestamp
     * of the post upload.
     *
     * @param userId - user who wants to upload a post
     * @param content - content of the post
     */

    public void uploadPost(int userId, String content) {
        try {
            if (content.isEmpty()) {
                logger.error("'Content' cannot be empty or blank.");
                return;
            }
            if (!userRegistrationService.checkUserRegistered(userId)) {
                logger.error("User with User ID {} has not registered with the system.", userId);
                return;
            }

            logger.debug("Received request from user with ID {} to upload the Post with content {}", userId, content);
            int currentPostNumber = getNumberOfPostsOfUser(userId) + 1;
            if (currentPostNumber == 0) {
                return;
            }

            logger.debug("Generating post ID for the post");
            String currentPostId = String.format("%03d", currentPostNumber);
            long postUploadTimestamp = System.currentTimeMillis();

            logger.debug("Formatting the date and time for the post");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh-mm a");
            String postUploadDateTime = formatter.format(LocalDateTime.now());
            String postedBy = userRegistrationService.usersRegistered.get(userId).getUserName();

            Post post = new Post(currentPostId, content, postedBy, postUploadDateTime, postUploadTimestamp);

            if (null == postsByUser || postsByUser.isEmpty() || null == postsByUser.get(userId)) {
                postsByUser.put(userId, new ArrayList<>());
            }

            logger.debug("Uploading post with post ID: {}", currentPostId);
            postsByUser.get(userId).add(post);
            allPosts.add(post);
            logger.info("Upload Successful with post ID: {}", currentPostId);
        } catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }

    private int getNumberOfPostsOfUser(int userId) {
        if(!userRegistrationService.checkUserRegistered(userId)) {
            logger.error("User with User ID {} has not registered with the system.", userId);
            return -1;
        }
        if(null == postsByUser || postsByUser.isEmpty() ||
               null == postsByUser.get(userId) || postsByUser.get(userId).isEmpty()) {
            return 0;
        }
        return postsByUser.get(userId).size();
    }

    public void like(Post post) {
        post.setLikes(post.getLikes() + 1);
        if(post.getDislikes() > 0) {
            post.setDislikes(post.getDislikes() - 1);
        }
    }

    public void dislike(Post post) {
        post.setDislikes(post.getDislikes() + 1);
        if(post.getLikes() > 0) {
            post.setLikes(post.getLikes() - 1);
        }
    }
}
