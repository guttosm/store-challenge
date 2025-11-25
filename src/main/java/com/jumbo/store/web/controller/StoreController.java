package com.jumbo.store.web.controller;

import com.jumbo.store.domain.service.StoreService;
import com.jumbo.store.web.contract.StoreControllerContract;
import com.jumbo.store.web.dto.NearestStoresResponse;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for store-related operations.
 * Follows Single Responsibility Principle - only handles HTTP requests/responses.
 * Requires authentication with customer role and read:store permission.
 */
@RestController
@RequestMapping("/stores")
@RequiredArgsConstructor
@Slf4j
public class StoreController implements StoreControllerContract {

    private final StoreService storeService;

    @Override
    @GetMapping("/nearest")
    @PreAuthorize("hasAuthority('SCOPE_read:store')")
    public NearestStoresResponse findNearestStores(BigDecimal latitude, BigDecimal longitude, Integer limit) {
        log.info("Received request to find nearest stores: lat={}, lon={}, limit={}", latitude, longitude, limit);
        return storeService.findNearestStores(latitude, longitude, limit);
    }
}
