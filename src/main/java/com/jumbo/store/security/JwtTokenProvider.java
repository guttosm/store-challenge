package com.jumbo.store.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * JWT Token provider for authentication.
 * Generates and validates JWT tokens with roles and permissions.
 */
@Component
@Slf4j
public class JwtTokenProvider {

    @Value("${jwt.secret:mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}") // 24 hours
    private long jwtExpiration;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Generates a JWT token for a user with roles and permissions.
     * Token expiration is configured via jwt.expiration property (default: 24 hours).
     *
     * @param username    the username to include in the token
     * @param roles       list of role authorities (e.g., "ROLE_CUSTOMER")
     * @param permissions list of permission strings (e.g., "read:store")
     * @return signed JWT token string
     */
    public String generateToken(String username, List<String> roles, List<String> permissions) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        Claims claims = Jwts.claims()
                .subject(username)
                .add("roles", roles)
                .add("permissions", permissions)
                .build();

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Validates a JWT token signature and expiration.
     *
     * @param token the JWT token to validate
     * @return true if token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Extracts username from JWT token.
     */
    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    /**
     * Extracts authorities from JWT token.
     * Permissions are prefixed with "SCOPE_" to match Spring Security's scope format.
     *
     * @param token the JWT token
     * @return list of authorities (roles + scoped permissions)
     */
    public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        @SuppressWarnings("unchecked")
        List<String> permissions = claims.get("permissions", List.class);

        List<SimpleGrantedAuthority> roleAuthorities =
                roles != null ? roles.stream().map(SimpleGrantedAuthority::new).toList() : List.of();

        List<SimpleGrantedAuthority> permissionAuthorities = permissions != null
                ? permissions.stream()
                        .map(permission -> new SimpleGrantedAuthority("SCOPE_" + permission))
                        .toList()
                : List.of();

        return Stream.concat(roleAuthorities.stream(), permissionAuthorities.stream())
                .toList();
    }
}
