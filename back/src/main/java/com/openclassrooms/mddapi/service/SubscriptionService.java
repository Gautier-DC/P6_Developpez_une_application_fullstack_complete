package com.openclassrooms.mddapi.service;

import org.springframework.security.core.Authentication;

import java.util.List;

public interface SubscriptionService {

    void subscribeToTheme(Authentication authentication, Long themeId);

    void unsubscribeFromTheme(Authentication authentication, Long themeId);

    List<Long> getUserSubscriptions(Authentication authentication);

    boolean isUserSubscribedToTheme(Authentication authentication, Long themeId);
}