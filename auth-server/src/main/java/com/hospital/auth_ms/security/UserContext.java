package com.hospital.auth_ms.security;

import java.util.Set;

public record UserContext(
        Long userId,
        String role,
        Set<String> permissions
) {

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }
}