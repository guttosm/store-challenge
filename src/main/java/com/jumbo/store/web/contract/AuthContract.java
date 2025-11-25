package com.jumbo.store.web.contract;

/**
 * Contract interfaces for authentication API requests and responses.
 * Uses Java records for immutability and type safety.
 */
public class AuthContract {

    /**
     * Login request contract.
     */
    public record LoginRequest(String username, String password) {}

    /**
     * Authentication response contract.
     */
    public record AuthResponse(String accessToken, String tokenType) {
        public AuthResponse {
            if (tokenType == null) {
                tokenType = "Bearer";
            }
        }
    }
}
