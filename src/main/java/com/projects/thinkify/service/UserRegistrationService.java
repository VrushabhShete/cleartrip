package com.projects.thinkify.service;

import com.projects.thinkify.controller.DriverController;
import com.projects.thinkify.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


@Service
public class UserRegistrationService {

    DriverController driver;

    Logger logger = LoggerFactory.getLogger(UserRegistrationService.class);

    Map<Integer, User> usersRegistered = new HashMap<>();

    /**
     * A method which takes username as a parameter, generates a sequentially unique id for the user,
     * depending on the number of the users registered and registers the user.
     *
     * @param userName - Name of the user which wants to register
     *
     */

    public void registerUser(String userName) {
        try {
            logger.debug("Request received to register a new User: {}", userName);
            if (userName.isEmpty()) {
                logger.error("Cannot register user with empty username.");
                return;
            }

            logger.debug("Generating unique ID for user: {}", userName);
            int userId = generateUserIdForNewUser();
            User user = new User(userId, userName, new HashSet<>(), new HashSet<>());

            logger.debug("Registering user: {} with ID: {}", userName, userId);
            usersRegistered.put(userId, user);
            logger.info("{} Registered!!", userName);
        } catch (Exception ex) {
            logger.error("Exception occurred. Message:{}", ex.getMessage(), ex);
            driver.init();
        }
    }
    
    private int generateUserIdForNewUser() {
        int totalUsersRegisted = usersRegistered.size();
        return totalUsersRegisted + 1;
    }

    public boolean checkUserRegistered(int id) {
        return usersRegistered.containsKey(id);
    }
}
