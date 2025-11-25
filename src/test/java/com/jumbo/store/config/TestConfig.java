package com.jumbo.store.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

@TestConfiguration
public class TestConfig {

    // Secret key for signing test JWTs - must match the one used in JwtTokenProvider
    private static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
            "testSecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong".getBytes(StandardCharsets.UTF_8));

    @Bean
    @Primary
    JwtDecoder testJwtDecoder() {
        return token -> {
            try {
                // Parse the actual JWT token using JJWT
                Claims claims = Jwts.parser()
                        .verifyWith(SECRET_KEY)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload();

                // Extract standard JWT claims
                String subject = claims.getSubject();
                Instant issuedAt = claims.getIssuedAt().toInstant();
                Instant expiresAt = claims.getExpiration().toInstant();

                // Extract custom claims
                @SuppressWarnings("unchecked")
                List<String> roles = (List<String>) claims.get("roles");
                @SuppressWarnings("unchecked")
                List<String> permissions = (List<String>) claims.get("permissions");

                // Create JWT claims map
                Map<String, Object> claimsMap = Map.of(
                        JwtClaimNames.SUB,
                        subject,
                        "roles",
                        roles != null ? roles : List.of(),
                        "permissions",
                        permissions != null ? permissions : List.of(),
                        JwtClaimNames.IAT,
                        issuedAt.getEpochSecond(),
                        JwtClaimNames.EXP,
                        expiresAt.getEpochSecond());

                return new Jwt(
                        token,
                        issuedAt,
                        expiresAt,
                        Map.of(
                                "alg", "HS256",
                                "typ", "JWT"),
                        claimsMap);
            } catch (Exception e) {
                throw new JwtException("Invalid JWT token: " + e.getMessage(), e);
            }
        };
    }

    // Helper method to create valid JWT tokens for tests
    public static String createTestJwt(String subject, List<String> roles, List<String> permissions) {
        Instant now = Instant.now();
        Date issuedAt = Date.from(now);
        Date expiresAt = Date.from(now.plus(1, ChronoUnit.HOURS));

        JwtBuilder builder = Jwts.builder()
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .claim("roles", roles != null ? roles : List.of())
                .claim("permissions", permissions != null ? permissions : List.of())
                .signWith(SECRET_KEY);

        return builder.compact();
    }
}
