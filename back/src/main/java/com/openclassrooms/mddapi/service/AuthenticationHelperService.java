package com.openclassrooms.mddapi.service;

import com.openclassrooms.mddapi.model.User;
import org.springframework.security.core.Authentication;

public interface AuthenticationHelperService {

    /**
     * Extract User from Spring Security Authentication object
     *
     * @param authentication Spring Security authentication object
     * @return User entity from the authentication principal
     */
    User getCurrentUser(Authentication authentication);

    /**
     * Extract user email from Spring Security Authentication object
     *
     * @param authentication Spring Security authentication object
     * @return User email from the authentication name
     */
    String getCurrentUserEmail(Authentication authentication);
}