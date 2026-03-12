package com.codexaa.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtProvider {

    private static final SecretKey key =
            Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());

    private static final long EXPIRATION_TIME = 86400000; // 1 day

    // Generate JWT Token
    public String generateToken(Authentication authentication) {

        String authorities = populateAuthorities(authentication.getAuthorities());

        return Jwts.builder()
                .setSubject(authentication.getName()) // email
                .claim("authorities", authorities)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(key)
                .compact();
    }

    // Extract email
    public String getEmailFromToken(String token) {

        Claims claims = extractClaims(token);

        return claims.getSubject();
    }

    // Extract role
    public String getRoleFromToken(String token) {

        Claims claims = extractClaims(token);

        return claims.get("authorities", String.class);
    }

    // Common method for extracting claims
    private Claims extractClaims(String token) {

        if (token == null) {
            throw new RuntimeException("JWT token is missing");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        token = token.trim();

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Convert authorities list to string
    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
    }
}