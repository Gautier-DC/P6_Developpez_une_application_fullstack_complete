package com.openclassrooms.mddapi.service.impl;

import java.util.ArrayList;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;

/**
 * Custom UserDetailsService implementation for JWT authentication
 * Separated from AuthService to avoid circular dependencies
 */
@Slf4j
@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
    
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Implementation of UserDetailsService interface
     * Used by Spring Security for authentication
     * Accepts both email and username as identifier
     */
    @Override
    public UserDetails loadUserByUsername(String emailOrUsername) throws UsernameNotFoundException {
        log.debug("Loading user by email or username: {}", emailOrUsername);

        Optional<User> userOptional = userRepository.findByEmailOrUsername(emailOrUsername, emailOrUsername);

        if (userOptional.isEmpty()) {
            log.warn("User not found with email or username: {}", emailOrUsername);
            throw new UsernameNotFoundException("User not found");
        }

        User user = userOptional.get();
        log.debug("User loaded successfully: {}", emailOrUsername);
        
        // Return Spring Security UserDetails object
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new ArrayList<>()) // Empty authorities for now
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build();
    }
}