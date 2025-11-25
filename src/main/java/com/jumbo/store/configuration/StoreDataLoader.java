package com.jumbo.store.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.store.domain.model.Store;
import com.jumbo.store.domain.repository.StoreRepository;
import java.io.InputStream;
import java.math.BigDecimal;
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
    private final ObjectMapper objectMapper;

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

    private void loadStores(InputStream inputStream) throws Exception {
        // Step 1: Parse and validate JSON structure
        JsonNode rootNode;
        try {
            rootNode = objectMapper.readTree(inputStream);
        } catch (Exception e) {
            log.error("Invalid JSON format: Failed to parse JSON file. {}", e.getMessage());
            return;
        }

        JsonNode storesNode = rootNode.get("stores");
        if (storesNode == null || !storesNode.isArray()) {
            log.error("Invalid JSON format: 'stores' array not found or is not an array");
            return;
        }

        log.info("JSON structure validated. Found {} stores in array.", storesNode.size());

        // Step 2: Parse all stores at once
        List<Store> parsedStores = parseAllStores(storesNode);
        log.info("Parsed {} valid stores from JSON.", parsedStores.size());

        if (parsedStores.isEmpty()) {
            log.warn("No valid stores found in JSON file.");
            return;
        }

        // Step 3: Filter out duplicates in bulk
        List<Store> storesToInsert = filterExistingStores(parsedStores);
        log.info(
                "Filtered {} stores to insert ({} duplicates skipped).",
                storesToInsert.size(),
                parsedStores.size() - storesToInsert.size());

        // Step 4: Bulk insert
        if (!storesToInsert.isEmpty()) {
            storeRepository.saveAll(storesToInsert);
            log.info("Successfully loaded {} stores into database.", storesToInsert.size());
        } else {
            log.info("All stores already exist in database. No new stores loaded.");
        }
    }

    private List<Store> parseAllStores(JsonNode storesNode) {
        return java.util.stream.StreamSupport.stream(storesNode.spliterator(), false)
                .map(this::parseStoreSafely)
                .filter(java.util.Objects::nonNull)
                .filter(this::isValidStore)
                .toList();
    }

    private Store parseStoreSafely(JsonNode storeNode) {
        try {
            return parseStore(storeNode);
        } catch (Exception e) {
            log.debug("Failed to parse store node: {}", e.getMessage());
            return null;
        }
    }

    private boolean isValidStore(Store store) {
        return store.getUuid() != null
                && !store.getUuid().isBlank()
                && store.getLatitude() != null
                && store.getLongitude() != null;
    }

    private List<Store> filterExistingStores(List<Store> stores) {
        if (stores.isEmpty()) {
            return stores;
        }

        // Extract all UUIDs
        List<String> uuids = stores.stream()
                .map(Store::getUuid)
                .filter(uuid -> uuid != null && !uuid.isBlank())
                .distinct()
                .toList();

        if (uuids.isEmpty()) {
            return stores;
        }

        // Check which UUIDs already exist in database (single efficient query)
        java.util.Set<String> existingUuids = storeRepository.findExistingUuids(uuids);

        if (existingUuids.isEmpty()) {
            log.debug("No existing stores found. All {} stores will be inserted.", stores.size());
            return stores;
        }

        log.debug("Found {} existing stores by UUID. Filtering them out.", existingUuids.size());

        // Filter out stores with existing UUIDs
        return stores.stream()
                .filter(store -> !existingUuids.contains(store.getUuid()))
                .toList();
    }

    private Store parseStore(JsonNode storeNode) {
        try {
            String uuid = storeNode.get("uuid").asText();
            String latitudeStr = storeNode.get("latitude").asText();
            String longitudeStr = storeNode.get("longitude").asText();

            if (uuid == null
                    || uuid.isBlank()
                    || latitudeStr == null
                    || latitudeStr.isBlank()
                    || longitudeStr == null
                    || longitudeStr.isBlank()) {
                return null;
            }

            BigDecimal latitude = new BigDecimal(latitudeStr);
            BigDecimal longitude = new BigDecimal(longitudeStr);

            return Store.builder()
                    .uuid(uuid)
                    .addressName(getTextValue(storeNode, "addressName"))
                    .city(getTextValue(storeNode, "city"))
                    .postalCode(getTextValue(storeNode, "postalCode"))
                    .street(getTextValue(storeNode, "street"))
                    .street2(getTextValue(storeNode, "street2"))
                    .street3(getTextValue(storeNode, "street3"))
                    .latitude(latitude)
                    .longitude(longitude)
                    .complexNumber(getTextValue(storeNode, "complexNumber"))
                    .showWarningMessage(getBooleanValue(storeNode, "showWarningMessage"))
                    .todayOpen(getTextValue(storeNode, "todayOpen"))
                    .todayClose(getTextValue(storeNode, "todayClose"))
                    .locationType(getTextValue(storeNode, "locationType"))
                    .collectionPoint(getBooleanValue(storeNode, "collectionPoint"))
                    .sapStoreID(getTextValue(storeNode, "sapStoreID"))
                    .build();
        } catch (Exception e) {
            log.debug("Error parsing store node: {}", e.getMessage());
            return null;
        }
    }

    private String getTextValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        String value = fieldNode.asText();
        return value.isBlank() ? null : value;
    }

    private Boolean getBooleanValue(JsonNode node, String fieldName) {
        JsonNode fieldNode = node.get(fieldName);
        if (fieldNode == null || fieldNode.isNull()) {
            return null;
        }
        return fieldNode.asBoolean();
    }
}
