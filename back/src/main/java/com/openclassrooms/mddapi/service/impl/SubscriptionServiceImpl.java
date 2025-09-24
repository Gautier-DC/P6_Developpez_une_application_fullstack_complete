package com.openclassrooms.mddapi.service.impl;

import com.openclassrooms.mddapi.model.Subscription;
import com.openclassrooms.mddapi.model.Theme;
import com.openclassrooms.mddapi.model.User;
import com.openclassrooms.mddapi.repository.SubscriptionRepository;
import com.openclassrooms.mddapi.repository.ThemeRepository;
import com.openclassrooms.mddapi.service.AuthenticationHelperService;
import com.openclassrooms.mddapi.service.SubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@Transactional
public class SubscriptionServiceImpl implements SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private AuthenticationHelperService authenticationHelperService;

    @Override
    public void subscribeToTheme(Authentication authentication, Long themeId) {
        log.info("Subscribing user to theme with ID: {}", themeId);

        User user = authenticationHelperService.getCurrentUser(authentication);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Theme not found with ID: " + themeId));

        // Check if already subscribed
        if (subscriptionRepository.existsByUserAndTheme(user, theme)) {
            log.warn("User {} is already subscribed to theme {}", user.getEmail(), themeId);
            throw new IllegalStateException("User is already subscribed to this theme");
        }

        Subscription subscription = new Subscription(user, theme);
        subscriptionRepository.save(subscription);

        log.info("User {} successfully subscribed to theme {}", user.getEmail(), themeId);
    }

    @Override
    public void unsubscribeFromTheme(Authentication authentication, Long themeId) {
        log.info("Unsubscribing user from theme with ID: {}", themeId);

        User user = authenticationHelperService.getCurrentUser(authentication);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Theme not found with ID: " + themeId));

        if (!subscriptionRepository.existsByUserAndTheme(user, theme)) {
            log.warn("User {} is not subscribed to theme {}", user.getEmail(), themeId);
            throw new IllegalStateException("User is not subscribed to this theme");
        }

        subscriptionRepository.deleteByUserAndTheme(user, theme);
        log.info("User {} successfully unsubscribed from theme {}", user.getEmail(), themeId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getUserSubscriptions(Authentication authentication) {
        log.info("Getting subscriptions for user");

        User user = authenticationHelperService.getCurrentUser(authentication);
        List<Long> subscriptions = subscriptionRepository.findThemeIdsByUser(user);

        log.info("Found {} subscriptions for user {}", subscriptions.size(), user.getEmail());
        return subscriptions;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserSubscribedToTheme(Authentication authentication, Long themeId) {
        User user = authenticationHelperService.getCurrentUser(authentication);
        Theme theme = themeRepository.findById(themeId)
                .orElseThrow(() -> new IllegalArgumentException("Theme not found with ID: " + themeId));

        return subscriptionRepository.existsByUserAndTheme(user, theme);
    }
}