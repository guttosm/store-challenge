package com.jumbo.store.domain.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Calculates distances between geographic coordinates using the Haversine formula.
 * Follows Single Responsibility Principle - only responsible for distance calculations.
 */
@Component
@Slf4j
public class DistanceCalculator {

    private static final int EARTH_RADIUS_KM = 6371;
    private static final int DECIMAL_PLACES = 2;

    /**
     * Calculates the distance between two points on Earth using the Haversine formula.
     *
     * @param lat1 latitude of the first point
     * @param lon1 longitude of the first point
     * @param lat2 latitude of the second point
     * @param lon2 longitude of the second point
     * @return distance in kilometers, rounded to 2 decimal places
     */
    public double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a = calculateHaversineFormula(lat1Rad, lat2Rad, deltaLat, deltaLon);
        double c = calculateCentralAngle(a);
        double distance = EARTH_RADIUS_KM * c;

        return roundDistance(distance);
    }

    /**
     * Calculates the Haversine formula component 'a'.
     */
    private double calculateHaversineFormula(double lat1Rad, double lat2Rad, double deltaLat, double deltaLon) {
        return Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
    }

    /**
     * Calculates the central angle 'c' from the Haversine formula.
     */
    private double calculateCentralAngle(double a) {
        return 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    /**
     * Rounds distance to specified decimal places.
     */
    private double roundDistance(double distance) {
        return BigDecimal.valueOf(distance)
                .setScale(DECIMAL_PLACES, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
