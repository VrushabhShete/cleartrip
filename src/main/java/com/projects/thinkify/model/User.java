package com.projects.thinkify.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@Component
public class User {

    private int userId;

    private String userName;

    private Map<Integer, List<Post>> postsOfUser;

    private Set<Integer> following;

    private Set<Integer> followers;

    private Map<Integer, Map<Integer, Set<String>>> likedPosts;

    private Map<Integer, Map<Integer, Set<String>>> dislikedPosts;


    public User(int userId, String userName, Set<Integer> followers, Set<Integer> following) {
        this.userId = userId;
        this.userName = userName;
        this.followers = followers;
        this.following = following;
    }
}
