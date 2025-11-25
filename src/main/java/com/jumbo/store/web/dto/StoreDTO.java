package com.jumbo.store.web.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Store information.
 * Uses Java record for immutability and conciseness.
 */
public record StoreDTO(
        String uuid,
        String addressName,
        String city,
        String postalCode,
        String street,
        String street2,
        String street3,
        BigDecimal latitude,
        BigDecimal longitude,
        String complexNumber,
        Boolean showWarningMessage,
        String todayOpen,
        String todayClose,
        String locationType,
        Boolean collectionPoint,
        String sapStoreID,
        Double distanceInKm) {}
