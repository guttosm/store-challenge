package com.jumbo.store.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * User roles in the system.
 */
@Getter
@RequiredArgsConstructor
public enum Role {
    CUSTOMER("ROLE_CUSTOMER");

    private final String authority;
}
