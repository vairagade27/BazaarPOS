package com.codexaa.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JwtProvider {

    private static final SecretKey key =
            Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    /**
     * Generate JWT Token from Authentication object
     */
    public String generateToken(Authentication authentication) {
        // Convert authorities to a List of Strings
        List<String> authList = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(authentication.getName()) // email
                .claim("authorities", authList) // ✅ Save as LIST
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Generate JWT Token from User Email and Role (PRIMARY METHOD)
     */
    public String generateTokenFromUser(String email, String role) {
        // ✅ Create a List containing the single role
        List<String> authList = new ArrayList<>();
        authList.add(role);

        return Jwts.builder()
                .setSubject(email)
                .claim("authorities", authList) // ✅ Save as LIST
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    /**
     * Extract email from JWT token
     */
    public String getEmailFromToken(String token) {
        Claims claims = extractClaims(token);
        return claims.getSubject();
    }

    /**
     * Extract authorities/role from JWT token as a List
     */
    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = extractClaims(token);

        // Try to get as List first (new format)
        Object authObject = claims.get("authorities");

        if (authObject instanceof List) {
            return (List<String>) authObject;
        } else if (authObject instanceof String) {
            // Fallback for old format (comma-separated string)
            String authStr = (String) authObject;
            if (authStr == null || authStr.isEmpty()) {
                return new ArrayList<>();
            }
            return List.of(authStr.split(","));
        }

        return new ArrayList<>();
    }

    /**
     * Validate if JWT token is valid
     */
    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Extract claims from JWT token
     */
    private Claims extractClaims(String token) {
        if (token == null) {
            throw new RuntimeException("JWT token is missing");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        token = token.trim();

        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }
}