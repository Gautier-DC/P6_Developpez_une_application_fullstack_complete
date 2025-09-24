package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.UserRepository;
import com.openclassrooms.mddapi.service.AuthenticationHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationHelperServiceImpl implements AuthenticationHelperService {

    private final UserRepository userRepository;

    public AuthenticationHelperServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("Authentication or principal is null");
            throw new IllegalArgumentException("Authentication is required");
        }

        // Handle UserDetails from JWT authentication
        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String email = userDetails.getUsername();
            log.debug("Extracting user by email: {} from UserDetails", email);

            return userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("User not found with email: {}", email);
                        return new IllegalStateException("User not found: " + email);
                    });
        }

        // Fallback for direct User objects (if used elsewhere)
        if (authentication.getPrincipal() instanceof User user) {
            log.debug("Extracted user: {} from authentication", user.getEmail());
            return user;
        }

        log.error("Principal is not an instance of User or UserDetails: {}", authentication.getPrincipal().getClass());
        throw new IllegalStateException("Invalid authentication principal type");
    }

    @Override
    public String getCurrentUserEmail(Authentication authentication) {
        if (authentication == null) {
            log.warn("Authentication is null");
            throw new IllegalArgumentException("Authentication is required");
        }

        String email = authentication.getName();
        log.debug("Extracted email: {} from authentication", email);
        return email;
    }
}