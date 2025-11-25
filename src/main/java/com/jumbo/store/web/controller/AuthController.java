package com.jumbo.store.web.controller;

import com.jumbo.store.security.JwtTokenProvider;
import com.jumbo.store.security.model.Permission;
import com.jumbo.store.security.model.Role;
import com.jumbo.store.web.contract.AuthContract;
import com.jumbo.store.web.contract.AuthControllerContract;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller for token generation.
 * Provides endpoints for authentication and token generation.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthControllerContract {

    private final JwtTokenProvider tokenProvider;

    @Override
    public ResponseEntity<AuthContract.AuthResponse> login(@RequestBody AuthContract.LoginRequest request) {
        log.info("Login request for user: {}", request.username());

        // In a real application, validate credentials against a user service/database
        // For this demo, we'll generate a token with customer role and read:store permission
        String token = tokenProvider.generateToken(
                request.username(),
                List.of(Role.CUSTOMER.getAuthority()),
                List.of(Permission.READ_STORE.getPermission()));

        return ResponseEntity.ok(new AuthContract.AuthResponse(token, "Bearer"));
    }
}
