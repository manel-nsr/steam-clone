package com.example.steam_clone.service;

import com.example.steam_clone.model.User;
import com.example.steam_clone.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Attempting to load user: " + username); // Debug log
        
        // Try to find user by username first, then by email
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(username);
        }
        
        if (userOptional.isEmpty()) {
            System.out.println("User not found: " + username); // Debug log
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }
        
        User user = userOptional.get();
        System.out.println("User found: " + user.getUsername() + ", Password hash: " + user.getPassword()); // Debug log
        
        // Check if user is active
        boolean isActive = user.isActive();
        
        if (!isActive) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")))
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(!isActive)
                .build();
    }
}