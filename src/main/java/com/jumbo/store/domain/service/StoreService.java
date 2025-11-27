package com.jumbo.store.domain.service;

import com.jumbo.store.configuration.CacheConfig;
import com.jumbo.store.configuration.CircuitBreakerConfig;
import com.jumbo.store.domain.model.Coordinate;
import com.jumbo.store.domain.model.Store;
import com.jumbo.store.domain.repository.StoreRepository;
import com.jumbo.store.validation.LocationValidator;
import com.jumbo.store.web.dto.NearestStoresResponse;
import com.jumbo.store.web.dto.StoreDTO;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreService {

    private static final int DEFAULT_LIMIT = 5;

    private final StoreRepository storeRepository;
    private final DistanceCalculator distanceCalculator;
    private final CircuitBreakerFactory<?, ?> circuitBreakerFactory;
    private final LocationValidator locationValidator;

    /**
     * Finds the nearest stores to a given location.
     * Uses Haversine formula to calculate the distance between two points on Earth.
     * Caches the store list to reduce database load.
     * Protected by circuit breaker to handle database failures gracefully.
     *
     * @return List of all stores, or empty list if database is unavailable
     */
    @Transactional(readOnly = true)
    @Cacheable(value = CacheConfig.STORES_CACHE, key = "'all-stores'")
    public List<Store> getAllStores() {
        log.debug("Loading all stores from database");

        CircuitBreaker circuitBreaker =
                circuitBreakerFactory.create(CircuitBreakerConfig.STORE_SERVICE_CIRCUIT_BREAKER);

        Supplier<List<Store>> storeSupplier = () -> {
            log.debug("Executing database query for stores");
            return storeRepository.findAll();
        };

        return circuitBreaker.run(storeSupplier, throwable -> {
            log.error(
                    "Circuit breaker opened - database unavailable. Returning empty list. Error: {}",
                    throwable.getMessage());
            return List.of();
        });
    }

    /**
     * Finds the nearest stores to a given location.
     *
     * @param latitude  the latitude of the location
     * @param longitude the longitude of the location
     * @param limit     the maximum number of stores to return (default: 5)
     * @return NearestStoresResponse containing the list of nearest stores with distances
     */
    @Transactional(readOnly = true)
    public NearestStoresResponse findNearestStores(BigDecimal latitude, BigDecimal longitude, Integer limit) {
        log.debug("Finding nearest stores to location: lat={}, lon={}, limit={}", latitude, longitude, limit);

        locationValidator.validate(latitude, longitude);
        Coordinate coordinate = new Coordinate(latitude, longitude);
        int storeLimit = determineLimit(limit);
        List<Store> allStores = getAllStores();

        if (allStores.isEmpty()) {
            log.warn("No stores found - circuit breaker may be open or database unavailable");
            return createEmptyResponse();
        }

        List<StoreDTO> nearestStores = calculateNearestStores(allStores, coordinate, storeLimit);
        log.info("Found {} nearest stores", nearestStores.size());

        return new NearestStoresResponse(nearestStores, nearestStores.size());
    }

    private int determineLimit(Integer limit) {
        return (limit != null && limit > 0) ? limit : DEFAULT_LIMIT;
    }

    private NearestStoresResponse createEmptyResponse() {
        return new NearestStoresResponse(List.of(), 0);
    }

    private List<StoreDTO> calculateNearestStores(List<Store> stores, Coordinate coordinate, int limit) {
        return stores.stream()
                .map(store -> calculateStoreWithDistance(store, coordinate))
                .sorted(Comparator.comparing(StoreDTO::distanceInKm, Comparator.nullsLast(Double::compareTo)))
                .limit(limit)
                .toList();
    }

    private StoreDTO calculateStoreWithDistance(Store store, Coordinate coordinate) {
        double distance = distanceCalculator.calculateDistance(
                coordinate.latitudeAsDouble(),
                coordinate.longitudeAsDouble(),
                store.getLatitude().doubleValue(),
                store.getLongitude().doubleValue());
        return store.toDTO(distance);
    }
}
