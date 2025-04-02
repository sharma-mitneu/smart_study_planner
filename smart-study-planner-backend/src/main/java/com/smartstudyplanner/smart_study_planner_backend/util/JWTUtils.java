package com.smartstudyplanner.smart_study_planner_backend.util;

import com.smartstudyplanner.smart_study_planner_backend.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

/**
 * Utility class for JWT token operations
 */
@Component
public class JWTUtils {

    private static final Logger log = LoggerFactory.getLogger(JWTUtils.class);
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * Extract username from token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extract expiration date from token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extract a specific claim from token
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extract all claims from token
     */
    private Claims extractAllClaims(String token) {
        log.info("Received token: " + token);
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            log.info("Claims extracted successfully: " + claims);
            return claims;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token", e);
            throw new RuntimeException("Token has expired");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token", e);
            throw new RuntimeException("Token is unsupported");
        } catch (MalformedJwtException e) {
            log.error("Malformed JWT token", e);
            throw new RuntimeException("Malformed token");
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Invalid JWT token", e);
            throw new RuntimeException("Invalid token");
        }
    }

        /**
         * Check if token is expired
         */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Generate token for user
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Add custom claims if needed
        if (userDetails instanceof User) {
            User user = (User) userDetails;
            claims.put("userId", user.getId());
            claims.put("email", user.getEmail());
            claims.put("role", "ROLE_" + user.getRole().name());
        }

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Create token with claims and subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 3600000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validate token for user
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Get signing key from secret
     */
    private Key getSigningKey() {
        String secret = "ZmYyYTQ0ZDU3YzJlNGM2YmY2ZmFkZmMzZWIyMjk0Mjc=";
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        log.info("Decoded key bytes: " + Arrays.toString(keyBytes));
        return Keys.hmacShaKeyFor(keyBytes);
    }

}