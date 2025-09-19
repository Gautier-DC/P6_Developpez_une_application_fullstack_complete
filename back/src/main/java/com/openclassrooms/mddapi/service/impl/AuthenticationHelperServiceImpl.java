package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.service.AuthenticationHelperService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthenticationHelperServiceImpl implements AuthenticationHelperService {

    @Override
    public User getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            log.warn("Authentication or principal is null");
            throw new IllegalArgumentException("Authentication is required");
        }

        if (!(authentication.getPrincipal() instanceof User)) {
            log.error("Principal is not an instance of User: {}", authentication.getPrincipal().getClass());
            throw new IllegalStateException("Invalid authentication principal type");
        }

        User user = (User) authentication.getPrincipal();
        log.debug("Extracted user: {} from authentication", user.getUsername());
        return user;
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