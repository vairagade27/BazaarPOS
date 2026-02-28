package com.codexaa.config;
public class JwtConstant {

    // 64 characters = 512 bits (SAFE)
    public static final String SECRET_KEY =
            "codexaa-super-secure-jwt-secret-key-256-bit-1234567890";

    public static final String JWT_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
}
