package com.jumbo.store.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jumbo.store.domain.model.Store;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class StoreJsonParser {

    private final ObjectMapper objectMapper;

    /**
     * Parses stores from JSON input stream.
     * Expects JSON format: {"stores": [...]}
     *
     * @param inputStream the JSON input stream
     * @return list of parsed stores (invalid entries are skipped)
     * @throws Exception if JSON parsing fails or format is invalid
     */
    public List<Store> parseStores(InputStream inputStream) throws Exception {
        JsonNode rootNode = objectMapper.readTree(inputStream);
        JsonNode storesNode = rootNode.get("stores");

        if (storesNode == null || !storesNode.isArray()) {
            throw new IllegalArgumentException("Invalid JSON format: 'stores' array not found or is not an array");
        }

        log.info("Found {} stores in JSON array", storesNode.size());
        return parseAllStores(storesNode);
    }

    private List<Store> parseAllStores(JsonNode storesNode) {
        return java.util.stream.StreamSupport.stream(storesNode.spliterator(), false)
                .map(this::parseStoreSafely)
                .filter(java.util.Objects::nonNull)
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

    private Store parseStore(JsonNode storeNode) {
        String uuid = getTextValue(storeNode, "uuid");
        String latitudeStr = getTextValue(storeNode, "latitude");
        String longitudeStr = getTextValue(storeNode, "longitude");

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
