package com.jumbo.store.validation;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for LocationValidator edge cases.
 */
class LocationValidatorTest {

    private LocationValidator validator;

    @BeforeEach
    void setUp() {
        validator = new LocationValidator();
    }

    @Test
    @DisplayName("Should accept valid boundary latitude -90")
    void testValidateLatitude_MinBoundary() {
        assertDoesNotThrow(() -> validator.validateLatitude(BigDecimal.valueOf(-90)));
    }

    @Test
    @DisplayName("Should accept valid boundary latitude 90")
    void testValidateLatitude_MaxBoundary() {
        assertDoesNotThrow(() -> validator.validateLatitude(BigDecimal.valueOf(90)));
    }

    @Test
    @DisplayName("Should reject latitude less than -90")
    void testValidateLatitude_BelowMin() {
        assertThatThrownBy(() -> validator.validateLatitude(BigDecimal.valueOf(-90.1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");
    }

    @Test
    @DisplayName("Should reject latitude greater than 90")
    void testValidateLatitude_AboveMax() {
        assertThatThrownBy(() -> validator.validateLatitude(BigDecimal.valueOf(90.1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude must be between");
    }

    @Test
    @DisplayName("Should reject null latitude")
    void testValidateLatitude_Null() {
        assertThatThrownBy(() -> validator.validateLatitude(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude parameter is required");
    }

    @Test
    @DisplayName("Should accept valid boundary longitude -180")
    void testValidateLongitude_MinBoundary() {
        assertDoesNotThrow(() -> validator.validateLongitude(BigDecimal.valueOf(-180)));
    }

    @Test
    @DisplayName("Should accept valid boundary longitude 180")
    void testValidateLongitude_MaxBoundary() {
        assertDoesNotThrow(() -> validator.validateLongitude(BigDecimal.valueOf(180)));
    }

    @Test
    @DisplayName("Should reject longitude less than -180")
    void testValidateLongitude_BelowMin() {
        assertThatThrownBy(() -> validator.validateLongitude(BigDecimal.valueOf(-180.1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");
    }

    @Test
    @DisplayName("Should reject longitude greater than 180")
    void testValidateLongitude_AboveMax() {
        assertThatThrownBy(() -> validator.validateLongitude(BigDecimal.valueOf(180.1)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude must be between");
    }

    @Test
    @DisplayName("Should reject null longitude")
    void testValidateLongitude_Null() {
        assertThatThrownBy(() -> validator.validateLongitude(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude parameter is required");
    }

    @Test
    @DisplayName("Should accept valid coordinates at boundaries")
    void testValidate_BoundaryValues() {
        assertDoesNotThrow(() -> validator.validate(BigDecimal.valueOf(-90), BigDecimal.valueOf(-180)));
        assertDoesNotThrow(() -> validator.validate(BigDecimal.valueOf(90), BigDecimal.valueOf(180)));
    }

    @Test
    @DisplayName("Should reject when latitude is null")
    void testValidate_NullLatitude() {
        assertThatThrownBy(() -> validator.validate(null, BigDecimal.valueOf(4.9041)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Latitude");
    }

    @Test
    @DisplayName("Should reject when longitude is null")
    void testValidate_NullLongitude() {
        assertThatThrownBy(() -> validator.validate(BigDecimal.valueOf(52.3676), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Longitude");
    }
}
