package com.openclassrooms.mddapi.service;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;

public interface JwtService {
    
    /**
     * Generate JWT token for authenticated user
     */
    String generateToken(String email);
    
    /**
     * Generate JWT token with additional claims
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    
    /**
     * Extract username (email) from JWT token
     */
    String extractUsername(String token);
    
    /**
     * Get username from JWT token (alias for extractUsername)
     */
    String getUsernameFromToken(String token);
    
    /**
     * Extract user ID from JWT token
     */
    Long extractUserId(String token);
    
    /**
     * Extract expiration date from JWT token
     */
    Date extractExpiration(String token);
    
    /**
     * Extract specific claim from JWT token
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    
    /**
     * Check if JWT token is expired
     */
    Boolean isTokenExpired(String token);
    
    /**
     * Validate JWT token against user details
     */
    Boolean validateToken(String token, UserDetails userDetails);
    
    /**
     * Validate JWT token (basic validation without user details)
     */
    Boolean validateToken(String token);
    
    /**
     * Get remaining time until token expiration
     */
    Long getTokenRemainingTime(String token);
    
    /**
     * Convert Date to LocalDateTime for easier handling
     */
    LocalDateTime dateToLocalDateTime(Date date);
}