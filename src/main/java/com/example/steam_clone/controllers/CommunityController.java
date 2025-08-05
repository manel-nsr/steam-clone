package com.example.steam_clone.controllers;

import com.example.steam_clone.model.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/community")
public class CommunityController {

    @GetMapping
    public String showCommunityPage(Model model) {
        // Mock list of posts (replace with real data later)
        List<Post> posts = Arrays.asList(
            new Post("My first post!", "I just joined Steam Clone!"),
            new Post("What are you playing?", "Let's share what games we're into right now."),
            new Post("Need help!", "Can't launch a game. Any ideas?")
        );

        model.addAttribute("posts", posts);
        return "community/community";
    }
}
