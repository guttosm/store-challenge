package com.jumbo.store.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record Coordinate(BigDecimal latitude, BigDecimal longitude) {

    public Coordinate {
        Objects.requireNonNull(latitude, "Latitude cannot be null");
        Objects.requireNonNull(longitude, "Longitude cannot be null");
    }

    public static Coordinate of(double latitude, double longitude) {
        return new Coordinate(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude));
    }

    public double latitudeAsDouble() {
        return latitude.doubleValue();
    }

    public double longitudeAsDouble() {
        return longitude.doubleValue();
    }
}
