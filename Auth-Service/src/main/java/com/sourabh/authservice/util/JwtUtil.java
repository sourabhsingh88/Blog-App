package com.sourabh.authservice.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long authExpiryMillis;
    private final long otpExpiryMillis;

    public JwtUtil(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiry.minutes}") int authExpiryMinutes,
            @Value("${jwt.otp.expiry.minutes}") int otpExpiryMinutes
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.authExpiryMillis = authExpiryMinutes * 60 * 1000L;
        this.otpExpiryMillis = otpExpiryMinutes * 60 * 1000L;
    }

    /* ===================== AUTH TOKEN ===================== */

    public String generateAuthToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .claim("type", "AUTH")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + authExpiryMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /* ===================== OTP BASED TOKENS ===================== */

    public String generateOtpToken(String email, String phone, String type) {

        JwtBuilder builder = Jwts.builder()
                .setSubject(email)
                .claim("type", type)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + otpExpiryMillis));

        if (phone != null) {
            builder.claim("phone", phone);
        }

        return builder.signWith(key, SignatureAlgorithm.HS256).compact();
    }

    /* ===================== VALIDATION ===================== */

    public Claims validateAndExtract(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token) {
        try {
            validateAndExtract(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public String extractEmail(String token) {
        return validateAndExtract(token).getSubject();
    }
}
