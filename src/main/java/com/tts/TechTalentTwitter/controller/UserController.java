package com.tts.TechTalentTwitter.controller;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tts.TechTalentTwitter.model.Tweet;
import com.tts.TechTalentTwitter.model.User;
import com.tts.TechTalentTwitter.service.TweetService;
import com.tts.TechTalentTwitter.service.UserService;

@Controller
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private TweetService tweetService;
    
    @GetMapping(value = "/users/{username}")
	public String getUser(@PathVariable(value="username") String username, Model model) {	
    	User user = userService.findByUsername(username);
	    List<Tweet> tweets = tweetService.findAllByUser(user);
	    User loggedInUser = userService.getLoggedInUser();
	    List<User> following = loggedInUser.getFollowing();
	    boolean isFollowing = false;
	    for (User followedUser : following) {
	        if (followedUser.getUsername().equals(username)) {
	            isFollowing = true;
	        }
	    }
	    boolean isSelfPage = loggedInUser.getUsername().equals(username);
	    model.addAttribute("isSelfPage", isSelfPage);
	    model.addAttribute("tweetList", tweets);
	    model.addAttribute("user", user);
	    model.addAttribute("following", isFollowing);
	    return "user";
	}
    
    private void SetTweetCounts(List<User> users, Model model) {
        HashMap<String,Integer> tweetCounts = new HashMap<>();
        for (User user : users) {
            List<Tweet> tweets = tweetService.findAllByUser(user);
            tweetCounts.put(user.getUsername(), tweets.size());
        }
        model.addAttribute("tweetCounts", tweetCounts);
    }
    
    private void SetFollowingStatus(List<User> users, List<User> usersFollowing, Model model) {
        HashMap<String,Boolean> followingStatus = new HashMap<>();
        String username = userService.getLoggedInUser().getUsername();
        for (User singleUser : users) {
            if(usersFollowing.contains(singleUser)) {
                followingStatus.put(singleUser.getUsername(), true);
            }else if (!singleUser.getUsername().equals(username)) {
                followingStatus.put(singleUser.getUsername(), false);
        	}
        }
        model.addAttribute("followingStatus", followingStatus);
    }


    
    @GetMapping(value = "/users")
    public String getUsers(Model model) {
        List<User> users = userService.findAll();
        model.addAttribute("users", users);
        SetTweetCounts(users, model);
        User loggedInUser = userService.getLoggedInUser();
        List<User> usersFollowing = loggedInUser.getFollowing();
        SetFollowingStatus(users, usersFollowing, model);
        return "users";
    }
    
}