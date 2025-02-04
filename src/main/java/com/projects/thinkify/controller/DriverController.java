package com.projects.thinkify.controller;

import com.projects.thinkify.service.FeedDisplayService;
import com.projects.thinkify.service.InteractionWithUserService;
import com.projects.thinkify.service.PostingService;
import com.projects.thinkify.service.UserRegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.Scanner;

@Component
@Controller
public class DriverController {

    @Autowired
    InteractionWithUserService interactionWithUserService;

    @Autowired
    PostingService postingService;

    @Autowired
    UserRegistrationService userRegistrationService;

    @Autowired
    FeedDisplayService feedDisplayService;

    public void init() {

        try {
            Scanner sc = new Scanner(System.in);

            while (true) {
                System.out.println("\nType your choice in the formats below");
                System.out.println("1. RegisterUser <user_name>");
                System.out.println("2. UploadPost <user_id> <post>");
                System.out.println("3. InteractionWithUser FOLLOW/UNFOLLOW <user_id1> <user_id2>");
                System.out.println("4. ShowFeed <user_id>");

                System.out.println("Enter your choice");
                String choice = sc.nextLine();

                String[] choiceString = choice.split(" ");

                switch (choiceString[0]) {
                    case "RegisterUser":
                        userRegistrationService.registerUser(choiceString[1]);
                        break;

                    case "UploadPost":
                        postingService.uploadPost(Integer.valueOf(choiceString[1]),
                                choice.substring(choice.indexOf(choiceString[2]), choice.length()));
                        break;

                    case "InteractionWithUser":
                        if (choiceString[1].equalsIgnoreCase("Follow")) {
                            interactionWithUserService.followUser(Integer.valueOf(choiceString[2]),
                                    Integer.valueOf(choiceString[3]));
                        }
                        if (choiceString[1].equalsIgnoreCase("Unfollow")) {
                            interactionWithUserService.unfollowUser(Integer.valueOf(choiceString[2]),
                                    Integer.valueOf(choiceString[3]));
                        }
                        if (choiceString[1].equalsIgnoreCase("Like")) {
                            interactionWithUserService.likePost(Integer.valueOf(choiceString[2]),
                                    Integer.valueOf(choiceString[3]), choice.substring(choice.indexOf(choiceString[4]), choice.length()));
                        }
                        if (choiceString[1].equalsIgnoreCase("Dislike")) {
                            interactionWithUserService.dislikePost(Integer.valueOf(choiceString[2]),
                                    Integer.valueOf(choiceString[3]), choice.substring(choice.indexOf(choiceString[4]), choice.length()));
                        }
                        break;

                    case "ShowFeed":
                        feedDisplayService.displayFeed(Integer.valueOf(choiceString[1]));
                        break;

                    default:
                        System.out.println("Please enter valid input");
                }
            }
        }
        catch (Exception ex){
            System.out.println("Invalid input, please enter only the required number of inputs. \n Please make sure that the format is correct.");
            init();
        }
    }
}
