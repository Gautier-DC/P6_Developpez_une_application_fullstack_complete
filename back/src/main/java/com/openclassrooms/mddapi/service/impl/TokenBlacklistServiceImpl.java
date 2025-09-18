package com.openclassrooms.mddapi.service.impl;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.openclassrooms.mddapi.service.TokenBlacklistService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    
    // Map: token -> expiration date
    private final ConcurrentHashMap<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    
    @Override
    public void blacklistToken(String token, Date expiration) {
        if (token == null || token.trim().isEmpty()) {
            log.warn("Attempted to blacklist null or empty token");
            return;
        }
        
        blacklistedTokens.put(token, expiration);
        log.debug("Token blacklisted successfully. Total blacklisted tokens: {}", blacklistedTokens.size());
    }
    
    @Override
    public boolean isTokenBlacklisted(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
        
        boolean isBlacklisted = blacklistedTokens.containsKey(token);
        
        if (isBlacklisted) {
            log.debug("Token found in blacklist");
        }
        
        return isBlacklisted;
    }
    
    @Override
    public void cleanupExpiredTokens() {
        Date now = new Date();
        int initialSize = blacklistedTokens.size();
        
        // Remove expired tokens
        blacklistedTokens.entrySet().removeIf(entry -> {
            Date expiration = entry.getValue();
            return expiration != null && expiration.before(now);
        });
        
        int removedCount = initialSize - blacklistedTokens.size();
        
        if (removedCount > 0) {
            log.info("Cleaned up {} expired tokens from blacklist. Remaining: {}", 
                    removedCount, blacklistedTokens.size());
        }
    }
    
    @Override
    public long getBlacklistedTokenCount() {
        return blacklistedTokens.size();
    }
    
    /**
     * Scheduled cleanup of expired tokens
     * Runs every hour to clean up expired tokens
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000ms
    public void scheduledCleanup() {
        log.debug("Starting scheduled cleanup of expired blacklisted tokens");
        cleanupExpiredTokens();
    }
}