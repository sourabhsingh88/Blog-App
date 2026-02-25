package com.asuni.blogservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    private static final long ACCESS_TOKEN_VALIDITY = 1000 * 60 * 15; // 15 min
    private static final long REFRESH_TOKEN_VALIDITY = 1000L * 60 * 60 * 24 * 7; // 7 days
    private static final long OTP_TOKEN_VALIDITY = 1000 * 60 * 5; // 5 min

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /* ================= ACCESS TOKEN ================= */

    public String generateAccessToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .claim("type", "ACCESS")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ================= REFRESH TOKEN ================= */

    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("type", "REFRESH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ================= OTP TOKEN ================= */

    public String generateOtpToken(String email, String phone, String type) {
        return Jwts.builder()
                .setSubject(email)
                .claim("phone", phone)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + OTP_TOKEN_VALIDITY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ================= VALIDATION ================= */

    public String generateAuthToken(Long userId, String email) {

        return Jwts.builder()
                .setSubject(String.valueOf(userId))   // ✅ SUBJECT = USER ID
                .claim("email", email)                // optional
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Claims validateAndExtract(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Long extractUserId(String token) {
        return Long.parseLong(validateAndExtract(token).getSubject());
    }
}