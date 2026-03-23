package com.codexaa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtValidator jwtValidator;

    public SecurityConfig(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/error").permitAll()


                        .requestMatchers("/api/super-admin/**").hasAuthority("ROLE_ADMIN")


                        .requestMatchers("/api/store-admin/**").hasAnyAuthority(
                                "ROLE_STORE_ADMIN", "ROLE_STORE_MANAGER",
                                "ROLE_BRANCH_MANAGER", "ROLE_CASHIER")

                        .requestMatchers("/api/stores/**").hasAnyAuthority(
                                "ROLE_STORE_ADMIN", "ROLE_ADMIN")

                        .requestMatchers("/api/branch/**").hasAnyAuthority(
                                "ROLE_BRANCH_MANAGER", "ROLE_STORE_MANAGER",
                                "ROLE_STORE_ADMIN", "ROLE_CASHIER")


                        .requestMatchers("/api/store-manager/**").hasAnyAuthority(
                                "ROLE_STORE_MANAGER", "ROLE_STORE_ADMIN")


                        .requestMatchers("/api/cashier/**").hasAnyAuthority(
                                "ROLE_CASHIER", "ROLE_BRANCH_MANAGER",
                                "ROLE_STORE_MANAGER", "ROLE_STORE_ADMIN")

                        // Shift endpoints — all staff roles
                        .requestMatchers("/api/shifts/**").hasAnyAuthority(
                                "ROLE_CASHIER", "ROLE_BRANCH_MANAGER",
                                "ROLE_STORE_MANAGER", "ROLE_STORE_ADMIN")

                        // Employee endpoints
                        .requestMatchers("/api/employees/**").hasAnyAuthority(
                                "ROLE_STORE_ADMIN", "ROLE_STORE_MANAGER",
                                "ROLE_BRANCH_MANAGER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtValidator, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "http://localhost:3000"
        ));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}