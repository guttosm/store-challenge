package com.jumbo.store.web.contract;

import com.jumbo.store.web.dto.ErrorResponse;
import com.jumbo.store.web.dto.NearestStoresResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
@Tag(name = "Stores", description = "Store management endpoints")
@ApiResponse(
        responseCode = "400",
        description = "Bad request",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "401",
        description = "Unauthorized",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "403",
        description = "Forbidden",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "404",
        description = "Not found",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "405",
        description = "Method not allowed",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "500",
        description = "Internal server error",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
@ApiResponse(
        responseCode = "503",
        description = "Service unavailable",
        content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
public interface StoreControllerContract {

    @GetExchange("/nearest")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Find nearest stores",
            description =
                    "Returns the nearest stores to a given location based on latitude and longitude. Requires customer role with read:store permission.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(
            value = {
                @ApiResponse(
                        responseCode = "200",
                        description = "Successfully retrieved nearest stores",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = NearestStoresResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "success",
                                                    summary = "Successful retrieval of nearest stores",
                                                    value =
                                                            """
                                                    {
                                                      "stores": [
                                                        {
                                                          "uuid": "123e4567-e89b-12d3-a456-426614174000",
                                                          "addressName": "Jumbo Amsterdam Centrum",
                                                          "city": "Amsterdam",
                                                          "postalCode": "1012 AB",
                                                          "street": "Kalverstraat",
                                                          "street2": "123",
                                                          "street3": null,
                                                          "latitude": 52.3676,
                                                          "longitude": 4.9041,
                                                          "complexNumber": "33249-01",
                                                          "showWarningMessage": false,
                                                          "todayOpen": "08:00",
                                                          "todayClose": "22:00",
                                                          "locationType": "SupermarktPuP",
                                                          "collectionPoint": true,
                                                          "sapStoreID": "3324",
                                                          "distanceInKm": 0.5
                                                        },
                                                        {
                                                          "uuid": "223e4567-e89b-12d3-a456-426614174001",
                                                          "addressName": "Jumbo Amsterdam Oost",
                                                          "city": "Amsterdam",
                                                          "postalCode": "1091 AB",
                                                          "street": "Javastraat",
                                                          "street2": "45",
                                                          "street3": null,
                                                          "latitude": 52.3600,
                                                          "longitude": 4.9200,
                                                          "complexNumber": "33250-01",
                                                          "showWarningMessage": false,
                                                          "todayOpen": "08:00",
                                                          "todayClose": "22:00",
                                                          "locationType": "SupermarktPuP",
                                                          "collectionPoint": true,
                                                          "sapStoreID": "3325",
                                                          "distanceInKm": 1.2
                                                        }
                                                      ],
                                                      "count": 2
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "400",
                        description = "Invalid latitude or longitude parameters",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "invalidLocation",
                                                    summary = "Invalid location parameters",
                                                    value =
                                                            """
                                                    {
                                                      "status": "BAD_REQUEST",
                                                      "code": 400,
                                                      "message": "Invalid location provided. Latitude must be between -90 and 90, and longitude must be between -180 and 180.",
                                                      "details": [
                                                        "Invalid latitude: 100.0"
                                                      ],
                                                      "timestamp": "2025-01-15T14:33:45.123"
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "401",
                        description = "Unauthorized - Invalid or missing JWT token",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "unauthorized",
                                                    summary = "Unauthorized access",
                                                    value =
                                                            """
                                                    {
                                                      "status": "UNAUTHORIZED",
                                                      "code": 401,
                                                      "message": "You are not authorized.",
                                                      "details": [
                                                        "Authentication is required"
                                                      ],
                                                      "timestamp": "2025-01-15T14:33:45.123"
                                                    }
                                                    """)
                                        })),
                @ApiResponse(
                        responseCode = "403",
                        description = "Forbidden - Missing required permission",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "forbidden",
                                                    summary = "Forbidden access",
                                                    value =
                                                            """
                                                    {
                                                      "status": "FORBIDDEN",
                                                      "code": 403,
                                                      "message": "You are not authorized.",
                                                      "details": [
                                                        "Missing required permission: SCOPE_read:store"
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
                                        })),
                @ApiResponse(
                        responseCode = "503",
                        description = "Service unavailable - Circuit breaker is open",
                        content =
                                @Content(
                                        mediaType = "application/json",
                                        schema = @Schema(implementation = ErrorResponse.class),
                                        examples = {
                                            @ExampleObject(
                                                    name = "serviceUnavailable",
                                                    summary = "Service temporarily unavailable",
                                                    value =
                                                            """
                                                    {
                                                      "status": "SERVICE_UNAVAILABLE",
                                                      "code": 503,
                                                      "message": "Service temporarily unavailable. Please try again later.",
                                                      "details": [
                                                        "Circuit breaker is open"
                                                      ],
                                                      "timestamp": "2025-01-15T14:33:45.123"
                                                    }
                                                    """)
                                        }))
            })
    NearestStoresResponse findNearestStores(
            @Parameter(description = "Latitude coordinate (-90 to 90)", required = true, example = "52.3676")
                    @RequestParam
                    BigDecimal latitude,
            @Parameter(description = "Longitude coordinate (-180 to 180)", required = true, example = "4.9041")
                    @RequestParam
                    BigDecimal longitude,
            @Parameter(description = "Maximum number of stores to return (default: 5)", example = "5")
                    @RequestParam(required = false, defaultValue = "5")
                    Integer limit);
}
