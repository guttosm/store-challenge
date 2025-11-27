package com.jumbo.store.configuration;

import com.jumbo.store.domain.model.Store;
import com.jumbo.store.domain.repository.StoreRepository;
import java.io.InputStream;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@ConditionalOnProperty(name = "store.data.loader.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Slf4j
public class StoreDataLoader implements CommandLineRunner {

    private final StoreRepository storeRepository;
    private final StoreJsonParser storeJsonParser;
    private final StoreValidator storeValidator;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (storeRepository.count() > 0) {
            log.info("Stores already loaded in database. Skipping data load.");
            return;
        }

        log.info("Loading stores from stores.json...");

        ClassPathResource resource = new ClassPathResource("stores.json");
        if (!resource.exists()) {
            log.error("stores.json not found in classpath. Please ensure the file exists in src/main/resources/");
            return;
        }

        log.info("Found stores.json in classpath");
        try (InputStream inputStream = resource.getInputStream()) {
            loadStores(inputStream);
        }
    }

    private void loadStores(InputStream inputStream) {
        try {
            List<Store> parsedStores = storeJsonParser.parseStores(inputStream);
            List<Store> validStores = storeValidator.filterValidStores(parsedStores);

            log.info("Parsed {} valid stores from JSON", validStores.size());

            if (validStores.isEmpty()) {
                log.warn("No valid stores found in JSON file");
                return;
            }

            List<Store> storesToInsert = filterExistingStores(validStores);
            log.info(
                    "Filtered {} stores to insert ({} duplicates skipped)",
                    storesToInsert.size(),
                    validStores.size() - storesToInsert.size());

            if (!storesToInsert.isEmpty()) {
                storeRepository.saveAll(storesToInsert);
                log.info("Successfully loaded {} stores into database", storesToInsert.size());
            } else {
                log.info("All stores already exist in database. No new stores loaded");
            }
        } catch (Exception e) {
            log.error("Failed to load stores from JSON file: {}", e.getMessage(), e);
        }
    }

    private List<Store> filterExistingStores(List<Store> stores) {
        if (stores.isEmpty()) {
            return stores;
        }

        List<String> uuids = stores.stream()
                .map(Store::getUuid)
                .filter(uuid -> uuid != null && !uuid.isBlank())
                .distinct()
                .toList();

        if (uuids.isEmpty()) {
            return stores;
        }

        java.util.Set<String> existingUuids = storeRepository.findExistingUuids(uuids);

        if (existingUuids.isEmpty()) {
            log.debug("No existing stores found. All {} stores will be inserted", stores.size());
            return stores;
        }

        log.debug("Found {} existing stores by UUID. Filtering them out", existingUuids.size());

        return stores.stream()
                .filter(store -> !existingUuids.contains(store.getUuid()))
                .toList();
    }
}
