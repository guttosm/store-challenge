package com.jumbo.store.validation;

import java.math.BigDecimal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Validates location coordinates (latitude and longitude).
 * Follows Single Responsibility Principle - only responsible for location validation.
 */
@Component
@Slf4j
public class LocationValidator {

    private static final BigDecimal MIN_LATITUDE = BigDecimal.valueOf(-90);
    private static final BigDecimal MAX_LATITUDE = BigDecimal.valueOf(90);
    private static final BigDecimal MIN_LONGITUDE = BigDecimal.valueOf(-180);
    private static final BigDecimal MAX_LONGITUDE = BigDecimal.valueOf(180);

    /**
     * Validates latitude and longitude coordinates.
     *
     * @param latitude  the latitude to validate
     * @param longitude the longitude to validate
     * @throws IllegalArgumentException if coordinates are invalid
     */
    public void validate(BigDecimal latitude, BigDecimal longitude) {
        validateLatitude(latitude);
        validateLongitude(longitude);
    }

    /**
     * Validates latitude coordinate.
     *
     * @param latitude the latitude to validate
     * @throws IllegalArgumentException if latitude is null or out of range
     */
    public void validateLatitude(BigDecimal latitude) {
        if (latitude == null) {
            throw new IllegalArgumentException("Latitude parameter is required");
        }
        if (latitude.compareTo(MIN_LATITUDE) < 0 || latitude.compareTo(MAX_LATITUDE) > 0) {
            throw new IllegalArgumentException(
                    String.format("Latitude must be between %s and %s", MIN_LATITUDE, MAX_LATITUDE));
        }
    }

    /**
     * Validates longitude coordinate.
     *
     * @param longitude the longitude to validate
     * @throws IllegalArgumentException if longitude is null or out of range
     */
    public void validateLongitude(BigDecimal longitude) {
        if (longitude == null) {
            throw new IllegalArgumentException("Longitude parameter is required");
        }
        if (longitude.compareTo(MIN_LONGITUDE) < 0 || longitude.compareTo(MAX_LONGITUDE) > 0) {
            throw new IllegalArgumentException(
                    String.format("Longitude must be between %s and %s", MIN_LONGITUDE, MAX_LONGITUDE));
        }
    }
}
