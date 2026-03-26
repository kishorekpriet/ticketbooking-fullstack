package com.kishore.ticketbooking.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 🛑 THE MASTER KEY: In a real company, this is hidden in environment variables!
    // It must be at least 32 characters long for the algorithm to work.
    private final String SECRET_KEY_STRING = "MySuperSecretKeyForBookMyShowClone2026";
    private final Key SECRET_KEY = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());

    // 1. PRINT THE WRISTBAND (Generate Token)
    // When a user logs in successfully, we run this method to give them their token.
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email) // The "Name Tag" on the wristband
                .setIssuedAt(new Date(System.currentTimeMillis())) // Time it was printed
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expires in 10 hours
                .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // The unbreakable signature
                .compact();
    }

    // 2. READ THE NAME TAG (Extract Email)
    // When a user tries to book a seat, we use this to see WHO is wearing the wristband.
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 3. CHECK THE EXPIRATION (Validate Token)
    // Checks if the wristband is fake, tampered with, or older than 10 hours.
    public boolean isTokenValid(String token) {
        try {
            return !getClaims(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return false; // If they tampered with the token, it instantly fails.
        }
    }

    // A helper method to open up the token and look inside
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}