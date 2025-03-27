package com.smartstudyplanner.smart_study_planner_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Date;

/**
 * Utility methods for JWT token handling
 */
public class JWTUtils {

    // JWT secret key (in production, this should be kept in secure configuration)
    private static final Key JWT_KEY = Keys.hmacShaKeyFor("thisIsASecretKeyForJwtThatShouldBeInASecureConfigInProduction".getBytes());

    /**
     * Extract JWT token from Authorization header
     *
     * @param request HTTP request
     * @return Token string or null if not found/invalid
     */
    public static String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * Extract user ID from JWT token
     *
     * @param token JWT token
     * @return User ID or null if token is invalid
     */
    public static Long getUserIdFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(JWT_KEY)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();
            return body.get("userId", Long.class);
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Extract username from JWT token
     *
     * @param token JWT token
     * @return Username or null if token is invalid
     */
    public static String getUsernameFromToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(JWT_KEY)
                    .build()
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();
            return body.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Check if token is valid and not expired
     *
     * @param token JWT token
     * @return true if token is valid
     */
    public static boolean validateToken(String token) {
        try {
            Jws<Claims> claimsJws = Jwts.parserBuilder()
                    .setSigningKey(JWT_KEY)
                    .build()
                    .parseClaimsJws(token);

            Date expiration = claimsJws.getBody().getExpiration();
            return !expiration.before(new Date());
        } catch (JwtException e) {
            return false;
        }
    }
}

