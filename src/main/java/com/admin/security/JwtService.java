package com.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.security.Key;
import java.util.Date;

@Slf4j  // This annotation adds logging capability
@Service // This tells Spring this is a service class
public class JwtService {

    // Reading secret key from application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    // Reading token expiration from application.properties
    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    /**
     * Creates a JWT token for the given email
     * @param email User's email
     * @return JWT token string
     */
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)  // Put email in token
                .setIssuedAt(new Date()) // Token creation time
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration)) // Token expiry time
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Sign the token
                .compact(); // Build the token
    }

    /**
     * Checks if a token is valid
     * @param token JWT token to check
     * @param email Email to validate against
     * @return true if token is valid, false otherwise
     */
    public boolean isTokenValid(String token, String email) {
        try {
            String emailInToken = extractEmail(token);
            boolean isTokenExpired = isTokenExpired(token);

            // Token is valid if email matches and token is not expired
            return emailInToken.equals(email) && !isTokenExpired;
        } catch (Exception e) {
            log.error("Error validating token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Gets the email from the token
     * @param token JWT token
     * @return email from token
     */
    public String extractEmail(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getSubject(); // Email is stored in the Subject
    }

    /**
     * Checks if token has expired
     */
    private boolean isTokenExpired(String token) {
        Date expirationDate = extractAllClaims(token).getExpiration();
        return expirationDate.before(new Date());
    }

    /**
     * Gets all data from the token
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("Error reading token: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * Creates the key used to sign tokens
     */
    private Key getSigningKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
} 