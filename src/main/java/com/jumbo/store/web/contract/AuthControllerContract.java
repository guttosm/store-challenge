package com.jumbo.store.web.contract;

import com.jumbo.store.web.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
@Tag(name = "Authentication", description = "Authentication and authorization endpoints")
@ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public interface AuthControllerContract {

    @PostExchange("/login")
    @Operation(
            summary = "Generate JWT token",
            description =
                    "Generates a JWT token for customer role with read:store permission. "
                            + "Note: In this demo, credentials are not validated. Any username/password combination will generate a valid token.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully generated JWT token",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = AuthContract.AuthResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "success",
                                                    summary = "Successful token generation",
                                                    value =
                                                            """
                                                    {
                                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                                      "tokenType": "Bearer"
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid request payload",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "invalidRequest",
                                                    summary = "Invalid request",
                                                    value =
                                                            """
                                                    {
                                                      "status": "BAD_REQUEST",
                                                      "code": 400,
                                                      "message": "Invalid request payload",
                                                      "details": [
                                                        "Username is required"
                                                      ],
                                                      "timestamp": "2025-01-15T14:33:45.123"
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "500",
                        description = "Internal server error",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "internalError",
                                                    summary = "Internal server error",
                                                    value =
                                                            """
                                                    {
                                                      "status": "INTERNAL_SERVER_ERROR",
                                                      "code": 500,
                                                      "message": "Internal server error occurred.",
                                                      "details": [
                                                        "An unexpected error occurred"
                                                      ],
                                                      "timestamp": "2025-01-15T14:33:45.123"
                                                    }
                                                    """)
                                        }))
            })
    ResponseEntity<AuthContract.AuthResponse> login(@RequestBody AuthContract.LoginRequest request);
}
