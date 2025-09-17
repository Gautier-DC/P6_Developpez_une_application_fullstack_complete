package com.openclassrooms.mddapi.service;

import java.util.Date;

public interface TokenBlacklistService {
    
    /**
     * Add a token to the blacklist
     * 
     * @param token JWT token to blacklist
     * @param expiration Token expiration date for cleanup
     */
    void blacklistToken(String token, Date expiration);
    
    /**
     * Check if a token is blacklisted
     * 
     * @param token JWT token to check
     * @return true if token is blacklisted
     */
    boolean isTokenBlacklisted(String token);
    
    /**
     * Remove expired tokens from blacklist
     * Called periodically to clean up storage
     */
    void cleanupExpiredTokens();
    
    /**
     * Get the number of blacklisted tokens
     * For monitoring purposes
     * 
     * @return count of blacklisted tokens
     */
    long getBlacklistedTokenCount();
}