package com.jumbo.store.fixtures;

import com.jumbo.store.domain.model.Store;
import com.jumbo.store.domain.repository.StoreRepository;
import com.jumbo.store.fixture.StoreFixture;
import java.util.function.UnaryOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Helper class for setting up test data in the database.
 * Provides methods to insert stores and other entities for integration tests.
 */
@Component
public class RepoHelper {

    @Autowired
    private StoreRepository storeRepository;

    /**
     * Inserts a store using the default Amsterdam store fixture.
     *
     * @return the inserted store
     */
    public Store insertStore() {
        return insertStore(builder -> builder);
    }

    /**
     * Inserts a store with customizations.
     *
     * @param customizer function to customize the store builder
     * @return the inserted store
     */
    public Store insertStore(UnaryOperator<Store.StoreBuilder> customizer) {
        Store baseStore = StoreFixture.createAmsterdamStore();
        Store.StoreBuilder builder = Store.builder()
                .uuid(baseStore.getUuid())
                .addressName(baseStore.getAddressName())
                .city(baseStore.getCity())
                .postalCode(baseStore.getPostalCode())
                .street(baseStore.getStreet())
                .street2(baseStore.getStreet2())
                .street3(baseStore.getStreet3())
                .latitude(baseStore.getLatitude())
                .longitude(baseStore.getLongitude())
                .showWarningMessage(baseStore.getShowWarningMessage())
                .locationType(baseStore.getLocationType())
                .todayOpen(baseStore.getTodayOpen())
                .todayClose(baseStore.getTodayClose())
                .collectionPoint(baseStore.getCollectionPoint())
                .complexNumber(baseStore.getComplexNumber())
                .sapStoreID(baseStore.getSapStoreID());
        Store store = customizer.apply(builder).build();
        return storeRepository.save(store);
    }

    /**
     * Inserts multiple stores from fixtures.
     *
     * @param stores the stores to insert
     * @return list of inserted stores
     */
    public java.util.List<Store> insertStores(Store... stores) {
        return storeRepository.saveAll(java.util.Arrays.asList(stores));
    }

    /**
     * Deletes all stores from the database.
     */
    public void deleteAllStores() {
        storeRepository.deleteAll();
    }
}
