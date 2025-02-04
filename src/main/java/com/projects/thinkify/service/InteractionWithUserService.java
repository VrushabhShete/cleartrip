package com.projects.thinkify.service;

import com.projects.thinkify.controller.DriverController;
import com.projects.thinkify.model.Post;
import com.projects.thinkify.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InteractionWithUserService {

    Logger logger = LoggerFactory.getLogger(InteractionWithUserService.class);

    @Autowired
    PostingService postingService;

    @Autowired
    User user;

    DriverController driver;

    @Autowired
    UserRegistrationService userRegistrationService;

    Map<Integer, User> userData;

    /**
     *
     * @param followFromUserId - user who wants to follow another user
     * @param followToUserId - another user who is being followed
     *
     * followUser - A method which adds followFromUserId in the followers list of followToUser, and adds
     *                       followToUser in the following list of followFromUserId
     */
    public void followUser(int followFromUserId, int followToUserId) {
        try {
            if (!userRegistrationService.checkUserRegistered(followFromUserId)) {
                logger.error("User with id: {} has not registered with the system.", followFromUserId);
                return;
            }
            if (!userRegistrationService.checkUserRegistered(followToUserId)) {
                logger.error("User with id: {} has not registered with the system.", followToUserId);
                return;
            }
            if (checkIfUserAlreadyFollowed(followFromUserId, followToUserId)) {
                logger.error("User with id: {} already follows user {}", followFromUserId, followToUserId);
                return;
            }

            userRegistrationService.usersRegistered.get(followFromUserId).getFollowing().add(followToUserId);
            userRegistrationService.usersRegistered.get(followToUserId).getFollowers().add(followFromUserId);
            logger.info("Followed {}!!", userRegistrationService.usersRegistered.get(followToUserId).getUserName());
        }
        catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }


    /**
     *
     * @param unfollowFromUserId - user who wants to unfollow another user
     * @param unfollowToUserId - another user who is being unfollowed
     *
     * unfollowUser - A method which removes unfollowFromUserId from the followers list of followToUser, and removes
     *                       unfollowToUser from the following list of unfollowFromUserId
     */
    public void unfollowUser(int unfollowFromUserId, int unfollowToUserId) {

        try {
            if (!userRegistrationService.checkUserRegistered(unfollowFromUserId)) {
                logger.error("User with id: {} has not registered with the system.", unfollowFromUserId);
                return;
            }
            if (!userRegistrationService.checkUserRegistered(unfollowToUserId)) {
                logger.error("User with id: {} has not registered with the system.", unfollowToUserId);
                return;
            }
            if (!checkIfUserAlreadyUnfollowed(unfollowFromUserId, unfollowToUserId)) {
                logger.error("User with id: {} already unfollows user {}", unfollowFromUserId, unfollowToUserId);
                return;
            }

            userRegistrationService.usersRegistered.get(unfollowFromUserId).getFollowing().remove(unfollowToUserId);
            userRegistrationService.usersRegistered.get(unfollowToUserId).getFollowers().remove(unfollowFromUserId);
            logger.info("Unfollowed {}!!", userData.get(unfollowToUserId).getUserName());
        }
        catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }

    /**
     * A method which likes the post from the account of a user
     *
     * @param likerId - user who wants to like a post
     * @param userIdOfPostLiked - user whose post likerId wants to like
     * @param postLikedId - post id of the post which the liker wants to like
     */
    public void likePost(int likerId, int userIdOfPostLiked, String postLikedId) {
        try {
            if (!userRegistrationService.checkUserRegistered(likerId)) {
                logger.error("User with id: {} has not registered with the system.", likerId);
                return;
            }
            if (!userRegistrationService.checkUserRegistered(userIdOfPostLiked)) {
                logger.error("User with id: {} has not registered with the system.", userIdOfPostLiked);
                return;
            }
            logger.debug("Request received by user with ID: {} to like the post with ID: {} of userID: {}",
                    likerId, postLikedId, userIdOfPostLiked);

            Post targetPost = new Post();
            if (null != postingService.postsByUser && null != postingService.postsByUser.get(userIdOfPostLiked)
                    && !postingService.postsByUser.get(userIdOfPostLiked).isEmpty()) {
                for (Post post : postingService.postsByUser.get(userIdOfPostLiked)) {
                    if (null != post && post.getPostId().equals(postLikedId)) {
                        targetPost = post;
                    }
                }
            }

            if (null == targetPost.getPostId() || targetPost.getPostId().isEmpty()) {
                logger.error("No post found with the post ID: {} from the user: {}", postLikedId, userIdOfPostLiked);
                return;
            }

            logger.debug("Checking if the user already liked the post");
            if (checkIfUserAlreadyLikedPost(likerId, userIdOfPostLiked, postLikedId)) {
                logger.error("User with ID {} already likes the post {} of user with ID {}.", likerId, postLikedId, userIdOfPostLiked);
                return;
            } else {
                logger.debug("Liking post: {} from user: {}", postLikedId, userIdOfPostLiked);

                if (null == user.getLikedPosts() || user.getLikedPosts().isEmpty()) {
                    user.setLikedPosts(new HashMap<>());
                    user.getLikedPosts().put(likerId, new HashMap<>());
                }
                if (!user.getLikedPosts().get(likerId).containsKey(userIdOfPostLiked)) {
                    user.getLikedPosts().get(likerId).put(userIdOfPostLiked, new HashSet<>());
                }
                user.getLikedPosts().get(likerId).get(userIdOfPostLiked).add(postLikedId);

                if (null != user.getDislikedPosts() && null != user.getDislikedPosts().get(likerId) &&
                        null != user.getDislikedPosts().get(likerId).get(userIdOfPostLiked)
                        && !user.getDislikedPosts().get(likerId).get(userIdOfPostLiked).contains(postLikedId)) {

                    user.getDislikedPosts().get(likerId).get(userIdOfPostLiked).remove(postLikedId);
                }
            }

            postingService.like(targetPost);
            logger.info("Post Liked!!");
        }
        catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }


    /**
     * A method which dislikes the post from the account of a user
     *
     * @param dislikerId - user who wants to dislike a post
     * @param userIdOfPostDisliked - user whose post dislikerId wants to dislike
     * @param postDislikedId - post id of the post which the disliker wants to dislike
     */
    public void dislikePost(int dislikerId, int userIdOfPostDisliked, String postDislikedId) {

        try {
            if (!userRegistrationService.checkUserRegistered(dislikerId)) {
                logger.error("User with id: {} has not registered with the system.", dislikerId);
            }

            if (!userRegistrationService.checkUserRegistered(userIdOfPostDisliked)) {
                logger.error("User with id: {} has not registered with the system.", userIdOfPostDisliked);
            }

            logger.debug("Request received by user with ID: {} to dislike the post with ID: {} of userID: {}",
                    dislikerId, postDislikedId, userIdOfPostDisliked);

            Post targetPost = new Post();
            if (null != postingService.postsByUser && null != postingService.postsByUser.get(userIdOfPostDisliked)
                    && !postingService.postsByUser.get(userIdOfPostDisliked).isEmpty()) {
                for (Post post : postingService.postsByUser.get(userIdOfPostDisliked)) {
                    if (null != post && post.getPostId().equals(postDislikedId)) {
                        targetPost = post;
                        break;
                    }
                }
            }

            if (null == targetPost.getPostId() || targetPost.getPostId().isEmpty()) {
                logger.error("No post found with the post ID: {} from the user: {}", postDislikedId, userIdOfPostDisliked);
                return;
            }

            logger.debug("Checking if the user already disliked the post");
            if (checkIfUserAlreadyDislikedPost(dislikerId, userIdOfPostDisliked, postDislikedId)) {
                logger.error("User with ID {} already dislikes the post {} of user with ID {}.",
                        dislikerId, postDislikedId, userIdOfPostDisliked);
                return;
            } else {
                logger.debug("Disliking post: {} from user: {}", postDislikedId, userIdOfPostDisliked);

                if (null == user.getDislikedPosts() || user.getDislikedPosts().isEmpty()) {
                    user.setDislikedPosts(new HashMap<>());
                    user.getDislikedPosts().put(dislikerId, new HashMap<>());
                }
                if (!user.getDislikedPosts().get(dislikerId).containsKey(userIdOfPostDisliked)) {
                    user.getDislikedPosts().get(dislikerId).put(userIdOfPostDisliked, new HashSet<>());
                }
                user.getDislikedPosts().get(dislikerId).get(userIdOfPostDisliked).add(postDislikedId);

                if (null != user.getLikedPosts() && null != user.getLikedPosts().get(dislikerId) &&
                        null != user.getLikedPosts().get(dislikerId).get(userIdOfPostDisliked)
                        && !user.getDislikedPosts().get(dislikerId).get(userIdOfPostDisliked).contains(postDislikedId)) {

                    user.getDislikedPosts().get(dislikerId).get(userIdOfPostDisliked).remove(postDislikedId);
                }
            }
            postingService.dislike(targetPost);
            logger.info("Post Disliked!!");
        }
        catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }

    private boolean checkIfUserAlreadyLikedPost(int likerId, int userIdOfPostLiked, String postLikedId) {
        return (null != user.getLikedPosts() && !user.getLikedPosts().isEmpty() && user.getLikedPosts().get(likerId)
                .get(userIdOfPostLiked).contains(postLikedId));
    }

    private boolean checkIfUserAlreadyDislikedPost(int dislikerId, int userIdOfPostDisliked, String postDislikedId) {
        return (null != user.getDislikedPosts() && !user.getDislikedPosts().isEmpty() && user.getDislikedPosts().get(dislikerId)
                .get(userIdOfPostDisliked).contains(postDislikedId));
    }

    private boolean checkIfUserAlreadyFollowed(int followFromUserId, int followToUserId) {
        userData = userRegistrationService.usersRegistered;
        return userData.get(followFromUserId).getFollowing().contains(followToUserId);
    }

    private boolean checkIfUserAlreadyUnfollowed(int followFromUserId, int followToUserId) {
        userData = userRegistrationService.usersRegistered;
        return userData.get(followFromUserId).getFollowing().contains(followToUserId);
    }
}
