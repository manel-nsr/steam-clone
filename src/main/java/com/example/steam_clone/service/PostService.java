package com.example.steam_clone.service;

import com.example.steam_clone.model.Post;
import com.example.steam_clone.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {
    
    private final PostRepository postRepository;

    @Autowired
    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    // Get all posts
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    // Save a post
    public Post savePost(Post post) {
        return postRepository.save(post);
    }
}
