package com.hospital.auth_ms.dtos.users;

public record UserResponse(
        Long id,
        String email,
        Long roleId,
        String role,
        boolean active
) {}