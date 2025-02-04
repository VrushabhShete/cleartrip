package com.projects.thinkify.service;

import com.projects.thinkify.model.Post;
import com.projects.thinkify.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class FeedDisplayService {

    Logger logger = LoggerFactory.getLogger(FeedDisplayService.class);

    @Autowired
    User user;

    @Autowired
    Post post;

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    PostingService postingService;

    /**
     * Method to display all the posts in the feed of the user with userid, starting from the posts
     * from the users the user has followed, followed by the users not followed, both sorted recent first
     *
     * @param userId - id of the user whose feed needs to be displayed.
     */
    public void displayFeed(int userId) {

        if(!userRegistrationService.checkUserRegistered(userId)) {
            logger.error("The user with user id {} has not registered with the system.", userId);
            return;
        }

        logger.debug("Fetching feed to display for user ID: {}", userId);

        List<Post> postsInFollowing = getPostsToDisplayInFeed(userId);

        System.out.println("\nThe feed for user with ID " + userId);

        for(Post post : postsInFollowing) {
            System.out.println("\nUserName - " + post.getPostedBy());
            System.out.println("Likes - " + post.getLikes());
            System.out.println("Dislikes - " + post.getDislikes());
            System.out.println("Post - " + post.getPostContent());
            System.out.println("Post time - " + post.getPostUploadDateTime());
        }

        List<Post> allPosts = getPostsToDisplayInFeed(postsInFollowing);

        for(Post post : allPosts) {
            System.out.println("\nUserName - " + post.getPostedBy());
            System.out.println("Likes - " + post.getLikes());
            System.out.println("Dislikes - " + post.getDislikes());
            System.out.println("Post - " + post.getPostContent());
            System.out.println("Post time - " + post.getPostUploadDateTime() + "\n");
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.println("End of Feed");
        System.out.println("---------------------------------------------------------------------------");
    }

    private List<Post> getPostsToDisplayInFeed(int userId) {

        User feedUser = userRegistrationService.usersRegistered.get(userId);
        Set<Integer> usersFollowed = feedUser.getFollowing();

        List<Post> postsInFollowing = new ArrayList<>();
        for(Integer id : usersFollowed) {
            if(null == postingService.postsByUser.get(id) || postingService.postsByUser.get(id).isEmpty()) {
                continue;
            }
            postsInFollowing.addAll(postingService.postsByUser.get(id));
        }

        postsInFollowing.sort(Comparator.comparing(Post::getPostUploadTimestamp).reversed());
        return postsInFollowing;
    }

    private List<Post> getPostsToDisplayInFeed(List<Post> postsInFollowing) {

        List<Post> allPosts = new ArrayList<>(postingService.allPosts);
        allPosts.removeAll(postsInFollowing);
        allPosts.sort(Comparator.comparing(Post::getPostUploadTimestamp).reversed());

        return allPosts;
    }
}
