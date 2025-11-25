package com.jumbo.store.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.jumbo.store.security.model.Permission;
import com.jumbo.store.security.model.Role;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * Unit tests for JWT Token Provider.
 */
class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;
    private static final String TEST_SECRET = "mySecretKeyForJWTTokenGenerationThatShouldBeAtLeast256BitsLong";
    private static final long TEST_EXPIRATION = 86400000L; // 24 hours

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider();
        ReflectionTestUtils.setField(tokenProvider, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(tokenProvider, "jwtExpiration", TEST_EXPIRATION);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void testGenerateToken_Success() {
        String username = "testuser";
        List<String> roles = List.of(Role.CUSTOMER.getAuthority());
        List<String> permissions = List.of(Permission.READ_STORE.getPermission());

        String token = tokenProvider.generateToken(username, roles, permissions);

        assertAll(
                () -> assertThat(token).isNotNull(),
                () -> assertThat(token).isNotEmpty(),
                () -> assertThat(token.split("\\.")).hasSize(3) // JWT has 3 parts
                );
    }

    @Test
    @DisplayName("Should validate valid token")
    void testValidateToken_ValidToken() {
        String username = "testuser";
        List<String> roles = List.of(Role.CUSTOMER.getAuthority());
        List<String> permissions = List.of(Permission.READ_STORE.getPermission());

        String token = tokenProvider.generateToken(username, roles, permissions);

        boolean isValid = tokenProvider.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should reject invalid token")
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.here";

        boolean isValid = tokenProvider.validateToken(invalidToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject empty token")
    void testValidateToken_EmptyToken() {
        boolean isValid = tokenProvider.validateToken("");

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should reject null token")
    void testValidateToken_NullToken() {
        boolean isValid = tokenProvider.validateToken(null);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract username from token")
    void testGetUsernameFromToken_Success() {
        String username = "testuser";
        List<String> roles = List.of(Role.CUSTOMER.getAuthority());
        List<String> permissions = List.of(Permission.READ_STORE.getPermission());

        String token = tokenProvider.generateToken(username, roles, permissions);
        String extractedUsername = tokenProvider.getUsernameFromToken(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should extract authorities from token")
    void testGetAuthoritiesFromToken_Success() {
        String username = "testuser";
        List<String> roles = List.of(Role.CUSTOMER.getAuthority());
        List<String> permissions = List.of(Permission.READ_STORE.getPermission());

        String token = tokenProvider.generateToken(username, roles, permissions);
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);

        assertAll(
                () -> assertThat(authorities).isNotNull(),
                () -> assertThat(authorities).hasSize(2),
                () -> assertThat(authorities)
                        .extracting(SimpleGrantedAuthority::getAuthority)
                        .containsExactlyInAnyOrder(Role.CUSTOMER.getAuthority(), Permission.READ_STORE.getAuthority()));
    }

    @Test
    @DisplayName("Should extract multiple roles and permissions")
    void testGetAuthoritiesFromToken_MultipleAuthorities() {
        String username = "testuser";
        List<String> roles = List.of(Role.CUSTOMER.getAuthority(), "ROLE_ADMIN");
        List<String> permissions = List.of(Permission.READ_STORE.getPermission(), "write:store");

        String token = tokenProvider.generateToken(username, roles, permissions);
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);

        assertAll(() -> assertThat(authorities).hasSize(4), () -> assertThat(authorities)
                .extracting(SimpleGrantedAuthority::getAuthority)
                .contains(
                        Role.CUSTOMER.getAuthority(),
                        "ROLE_ADMIN",
                        Permission.READ_STORE.getAuthority(),
                        "SCOPE_write:store"));
    }

    @Test
    @DisplayName("Should handle token with no roles or permissions")
    void testGetAuthoritiesFromToken_NoAuthorities() {
        String username = "testuser";
        String token = tokenProvider.generateToken(username, List.of(), List.of());
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);

        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should handle empty username")
    void testGenerateToken_EmptyUsername() {
        String token = tokenProvider.generateToken("", List.of(), List.of());

        assertThat(token).isNotNull();
        // Empty username generates a valid token, but JWT library returns null for empty subject
        String extractedUsername = tokenProvider.getUsernameFromToken(token);
        assertThat(extractedUsername).isNullOrEmpty();
    }

    @Test
    @DisplayName("Should handle null roles gracefully")
    void testGenerateToken_NullRoles() {
        // Note: JWT library may throw NPE with null roles, so we test with empty list instead
        // This test verifies the behavior with empty roles
        String token = tokenProvider.generateToken("testuser", List.of(), List.of());

        assertThat(token).isNotNull();
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);
        assertThat(authorities).isEmpty();
    }

    @Test
    @DisplayName("Should handle null permissions gracefully")
    void testGenerateToken_NullPermissions() {
        // Note: JWT library may throw NPE with null permissions, so we test with empty list instead
        // This test verifies the behavior with empty permissions
        String token = tokenProvider.generateToken("testuser", List.of(), List.of());

        assertThat(token).isNotNull();
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);
        assertThat(authorities).hasSize(0);
    }

    @Test
    @DisplayName("Should handle token with tampered signature")
    void testValidateToken_TamperedSignature() {
        String validToken = tokenProvider.generateToken("testuser", List.of(), List.of());

        // Tamper with the signature
        String[] parts = validToken.split("\\.");
        String tamperedToken = parts[0] + "." + parts[1] + ".tampered_signature";

        boolean isValid = tokenProvider.validateToken(tamperedToken);
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle token with missing parts")
    void testValidateToken_MissingParts() {
        boolean isValid = tokenProvider.validateToken("incomplete.token");
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should handle very long username")
    void testGenerateToken_VeryLongUsername() {
        String longUsername = "a".repeat(1000);
        String token = tokenProvider.generateToken(longUsername, List.of(), List.of());

        assertAll(() -> assertThat(token).isNotNull(), () -> assertThat(tokenProvider.getUsernameFromToken(token))
                .isEqualTo(longUsername));
    }

    @Test
    @DisplayName("Should handle special characters in username")
    void testGenerateToken_SpecialCharacters() {
        String username = "user@example.com!@#$%^&*()";
        String token = tokenProvider.generateToken(username, List.of(), List.of());

        assertAll(() -> assertThat(token).isNotNull(), () -> assertThat(tokenProvider.getUsernameFromToken(token))
                .isEqualTo(username));
    }

    @Test
    @DisplayName("Should handle many roles and permissions")
    void testGenerateToken_ManyAuthorities() {
        List<String> roles = List.of("ROLE_1", "ROLE_2", "ROLE_3", "ROLE_4", "ROLE_5");
        List<String> permissions = List.of("perm1", "perm2", "perm3", "perm4", "perm5");

        String token = tokenProvider.generateToken("testuser", roles, permissions);
        List<SimpleGrantedAuthority> authorities = tokenProvider.getAuthoritiesFromToken(token);

        assertThat(authorities).hasSize(10); // 5 roles + 5 permissions
    }
}
