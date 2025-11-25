package com.jumbo.store.web.dto;

import java.util.List;

/**
 * Response DTO for nearest stores query.
 * Uses Java record for immutability and conciseness.
 */
public record NearestStoresResponse(List<StoreDTO> stores, Integer count) {
    public NearestStoresResponse {
        if (stores == null) {
            stores = List.of();
        }
        if (count == null) {
            count = stores.size();
        }
    }
}
