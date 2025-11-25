package com.jumbo.store.configuration;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Circuit Breaker configuration for Resilience4j.
 * Provides circuit breaker instances for different services.
 */
@Configuration
@Slf4j
public class CircuitBreakerConfig {

    public static final String STORE_SERVICE_CIRCUIT_BREAKER = "storeService";

    @Bean
    public CircuitBreaker storeServiceCircuitBreaker(CircuitBreakerRegistry circuitBreakerRegistry) {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(STORE_SERVICE_CIRCUIT_BREAKER);

        // Register event listeners for monitoring
        circuitBreaker
                .getEventPublisher()
                .onStateTransition(event -> {
                    log.warn(
                            "Circuit breaker state transition: {} -> {}",
                            event.getStateTransition().getFromState(),
                            event.getStateTransition().getToState());
                })
                .onError(event -> {
                    log.error("Circuit breaker error: {}", event.getThrowable().getMessage());
                })
                .onSuccess(event -> {
                    log.debug("Circuit breaker success: {}", event.getElapsedDuration());
                });

        return circuitBreaker;
    }
}
