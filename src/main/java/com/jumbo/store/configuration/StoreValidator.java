package com.jumbo.store.configuration;

import com.jumbo.store.domain.model.Store;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StoreValidator {

    /**
     * Validates that a store has all required fields for processing.
     * Required fields: uuid (non-blank), latitude, longitude.
     *
     * @param store the store to validate
     * @return true if store has all required fields, false otherwise
     */
    public boolean isValid(Store store) {
        return store != null
                && store.getUuid() != null
                && !store.getUuid().isBlank()
                && store.getLatitude() != null
                && store.getLongitude() != null;
    }

    public List<Store> filterValidStores(List<Store> stores) {
        return stores.stream().filter(this::isValid).toList();
    }
}
