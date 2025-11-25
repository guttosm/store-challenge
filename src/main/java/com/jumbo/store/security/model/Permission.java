package com.jumbo.store.security.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Permissions in the system.
 */
@Getter
@RequiredArgsConstructor
public enum Permission {
    READ_STORE("read:store");

    private final String permission;

    public static final String PREFIX = "SCOPE_";

    public String getAuthority() {
        return PREFIX + permission;
    }
}
