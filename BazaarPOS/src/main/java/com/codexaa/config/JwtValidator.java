package com.codexaa.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class JwtValidator extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    public JwtValidator(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Allow CORS preflight requests
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {

                // Extract email and authorities from JWT
                String email = jwtProvider.getEmailFromToken(token);
                String authoritiesStr = jwtProvider.getRoleFromToken(token);

                // Parse comma-separated authorities and create authority list
                List<SimpleGrantedAuthority> authorities = new ArrayList<>();

                if (authoritiesStr != null && !authoritiesStr.isEmpty()) {
                    String[] authArray = authoritiesStr.split(",");
                    for (String auth : authArray) {
                        String trimmedAuth = auth.trim();
                        if (!trimmedAuth.isEmpty()) {
                            authorities.add(new SimpleGrantedAuthority(trimmedAuth));
                        }
                    }
                }

                // Create authentication with all authorities
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                authorities
                        );

                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                System.err.println("❌ JWT Validation Failed: " + e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}