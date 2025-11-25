package com.jumbo.store.domain.service;

import com.jumbo.store.domain.model.Store;
import com.jumbo.store.web.dto.StoreDTO;
import org.springframework.stereotype.Component;

/**
 * Maps Store entities to DTOs.
 * Follows Single Responsibility Principle - only responsible for entity-to-DTO mapping.
 */
@Component
public class StoreMapper {

    /**
     * Converts a Store entity to StoreDTO with distance.
     *
     * @param store   the store entity
     * @param distance the calculated distance in kilometers
     * @return StoreDTO with distance information
     */
    public StoreDTO toDTO(Store store, double distance) {
        return new StoreDTO(
                store.getUuid(),
                store.getAddressName(),
                store.getCity(),
                store.getPostalCode(),
                store.getStreet(),
                store.getStreet2(),
                store.getStreet3(),
                store.getLatitude(),
                store.getLongitude(),
                store.getComplexNumber(),
                store.getShowWarningMessage(),
                store.getTodayOpen(),
                store.getTodayClose(),
                store.getLocationType(),
                store.getCollectionPoint(),
                store.getSapStoreID(),
                distance);
    }
}
