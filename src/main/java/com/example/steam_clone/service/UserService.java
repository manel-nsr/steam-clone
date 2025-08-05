package com.example.steam_clone.service;

import com.example.steam_clone.model.User;
import com.example.steam_clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register a new user
public User registerUser(User user) {
    // Encrypt password before saving
    String encodedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(encodedPassword);
    user.setActive(true);
    return userRepository.save(user);
}

    // Check if username exists
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    // Check if email exists
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    // Find user by username or email
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        // Try to find by username first
        Optional<User> user = userRepository.findByUsername(usernameOrEmail);
        if (user.isPresent()) {
            return user;
        }
        // If not found, try to find by email
        return userRepository.findByEmail(usernameOrEmail);
    }

    // Find user by ID
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // Delete user by ID
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    // Update user
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    // Method to fix existing passwords in database
public void fixExistingPasswords() {
    List<User> users = userRepository.findAll();
    for (User user : users) {
        // Only encrypt if password is not already encrypted (doesn't start with $2a$)
        if (!user.getPassword().startsWith("$2a$")) {
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            userRepository.save(user);
            System.out.println("Fixed password for user: " + user.getUsername());
        }
    }
}
}