package com.openclassrooms.mddapi.security;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.openclassrooms.mddapi.service.JwtService;
import com.openclassrooms.mddapi.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * JWT Authentication Filter
 * Intercepts all HTTP requests to check for JWT tokens in Authorization header
 * If valid token found, sets authentication in Spring Security context
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    
    private final JwtService jwtService;
    private final UserDetailsService customUserDetailsService;
    private final TokenBlacklistService tokenBlacklistService;
    
    public JwtAuthenticationFilter(JwtService jwtService, 
                                  UserDetailsService customUserDetailsService,
                                  TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
        log.info("✅ JwtAuthenticationFilter initialized with blacklist service");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        log.info("🔍 JwtAuthenticationFilter EXECUTING for: {}", request.getRequestURI());
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        
        log.debug("🔍 JWT Filter - Request URI: {}", request.getRequestURI());
        log.debug("🔍 JWT Filter - Auth header: {}", authHeader != null ? "Bearer ***" : "null");
        
        // Check if Authorization header exists and starts with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("🔍 JWT Filter - No valid auth header, skipping");
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract JWT token (remove "Bearer " prefix)
        jwt = authHeader.substring(7);
        
        try {
            // Check if token is blacklisted
            if (tokenBlacklistService != null && tokenBlacklistService.isTokenBlacklisted(jwt)) {
                log.warn("Attempted to use blacklisted token");
                filterChain.doFilter(request, response);
                return;
            }
            
            // Extract email from JWT token
            userEmail = jwtService.extractUsername(jwt);
            
            // If email is found and user is not already authenticated
            if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                
                // Load user details from database
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userEmail);
                
                // Validate token against user details
                if (jwtService.validateToken(jwt, userDetails)) {
                    
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    
                    // Set additional details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    
                    log.debug("JWT authentication successful for user: {}", userEmail);
                } else {
                    log.warn("Invalid JWT token for user: {}", userEmail);
                }
            }
        } catch (Exception e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}