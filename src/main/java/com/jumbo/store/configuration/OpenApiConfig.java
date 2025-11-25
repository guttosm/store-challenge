package com.jumbo.store.configuration;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import java.util.LinkedHashMap;
import java.util.List;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration.
 * Provides API documentation with JWT authentication support and global error responses.
 */
@Configuration
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT")
public class OpenApiConfig {

    private static final String STATUS = "status";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    private static final String DETAILS = "details";
    private static final String TIMESTAMP = "timestamp";

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Store Service API")
                        .version("1.0.0")
                        .description("REST API for finding nearest Jumbo stores to a given location")
                        .contact(new Contact().name("Store Service Team").email("support@jumbo.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName));
    }

    @Bean
    public OpenApiCustomizer globalErrorResponses() {
        return openApi -> openApi.getPaths().values().forEach(pathItem -> pathItem.readOperations()
                .forEach(operation -> {
                    operation.getResponses().addApiResponse("400", badRequestResponse());
                    operation.getResponses().addApiResponse("401", unauthorizedResponse());
                    operation.getResponses().addApiResponse("403", forbiddenResponse());
                    operation.getResponses().addApiResponse("404", notFoundResponse());
                    operation.getResponses().addApiResponse("405", methodNotAllowedResponse());
                    operation.getResponses().addApiResponse("500", internalServerErrorResponse());
                    operation.getResponses().addApiResponse("503", serviceUnavailableResponse());
                }));
    }

    private ApiResponse badRequestResponse() {
        LinkedHashMap<String, Object> badRequestProperties = new LinkedHashMap<>();
        badRequestProperties.put(STATUS, "BAD_REQUEST");
        badRequestProperties.put(CODE, 400);
        badRequestProperties.put(
                MESSAGE,
                "Invalid location provided. Latitude must be between -90 and 90, and longitude must be between -180 and 180.");
        badRequestProperties.put(DETAILS, List.of("Invalid latitude: 100.0"));
        badRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse("Bad Request", "validationError", "Validation error example", badRequestProperties);
    }

    private ApiResponse unauthorizedResponse() {
        LinkedHashMap<String, Object> unauthorizedRequestProperties = new LinkedHashMap<>();
        unauthorizedRequestProperties.put(STATUS, "UNAUTHORIZED");
        unauthorizedRequestProperties.put(CODE, 401);
        unauthorizedRequestProperties.put(MESSAGE, "You are not authorized.");
        unauthorizedRequestProperties.put(DETAILS, List.of("Authentication is required"));
        unauthorizedRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse("Unauthorized", "unauthorized", "Unauthorized access", unauthorizedRequestProperties);
    }

    private ApiResponse forbiddenResponse() {
        LinkedHashMap<String, Object> forbiddenRequestProperties = new LinkedHashMap<>();
        forbiddenRequestProperties.put(STATUS, "FORBIDDEN");
        forbiddenRequestProperties.put(CODE, 403);
        forbiddenRequestProperties.put(MESSAGE, "You are not authorized.");
        forbiddenRequestProperties.put(DETAILS, List.of("Missing required permission: SCOPE_read:store"));
        forbiddenRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse("Forbidden", "forbidden", "Forbidden access", forbiddenRequestProperties);
    }

    private ApiResponse notFoundResponse() {
        LinkedHashMap<String, Object> notFoundRequestProperties = new LinkedHashMap<>();
        notFoundRequestProperties.put(STATUS, "NOT_FOUND");
        notFoundRequestProperties.put(CODE, 404);
        notFoundRequestProperties.put(MESSAGE, "Resource not found");
        notFoundRequestProperties.put(DETAILS, List.of());
        notFoundRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse("Not Found", "notFound", "Entity not found", notFoundRequestProperties);
    }

    private ApiResponse methodNotAllowedResponse() {
        LinkedHashMap<String, Object> methodNotAllowedRequestProperties = new LinkedHashMap<>();
        methodNotAllowedRequestProperties.put(STATUS, "METHOD_NOT_ALLOWED");
        methodNotAllowedRequestProperties.put(CODE, 405);
        methodNotAllowedRequestProperties.put(MESSAGE, "Method isn't allowed.");
        methodNotAllowedRequestProperties.put(
                DETAILS, List.of("HTTP method 'POST' is not supported for this endpoint"));
        methodNotAllowedRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse(
                "Method Not Allowed",
                "methodNotAllowed",
                "HTTP method not supported",
                methodNotAllowedRequestProperties);
    }

    private ApiResponse internalServerErrorResponse() {
        LinkedHashMap<String, Object> internalServerErrorRequestProperties = new LinkedHashMap<>();
        internalServerErrorRequestProperties.put(STATUS, "INTERNAL_SERVER_ERROR");
        internalServerErrorRequestProperties.put(CODE, 500);
        internalServerErrorRequestProperties.put(MESSAGE, "Internal server error occurred.");
        internalServerErrorRequestProperties.put(DETAILS, List.of("An unexpected error occurred"));
        internalServerErrorRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse(
                "Internal Server Error",
                "internalServerError",
                "Unexpected error",
                internalServerErrorRequestProperties);
    }

    private ApiResponse serviceUnavailableResponse() {
        LinkedHashMap<String, Object> serviceUnavailableRequestProperties = new LinkedHashMap<>();
        serviceUnavailableRequestProperties.put(STATUS, "SERVICE_UNAVAILABLE");
        serviceUnavailableRequestProperties.put(CODE, 503);
        serviceUnavailableRequestProperties.put(MESSAGE, "Service temporarily unavailable. Please try again later.");
        serviceUnavailableRequestProperties.put(DETAILS, List.of("Circuit breaker is open"));
        serviceUnavailableRequestProperties.put(TIMESTAMP, "2025-01-15T14:33:45.123");
        return buildApiResponse(
                "Service Unavailable",
                "serviceUnavailable",
                "Service temporarily unavailable",
                serviceUnavailableRequestProperties);
    }

    private ApiResponse buildApiResponse(
            String description, String exampleKey, String exampleSummary, LinkedHashMap<String, Object> exampleValue) {
        return new ApiResponse()
                .description(description)
                .content(new Content()
                        .addMediaType(
                                "application/json",
                                new MediaType()
                                        .schema(new Schema<>().$ref("#/components/schemas/ErrorResponse"))
                                        .addExamples(
                                                exampleKey,
                                                new Example()
                                                        .summary(exampleSummary)
                                                        .value(exampleValue))));
    }
}
