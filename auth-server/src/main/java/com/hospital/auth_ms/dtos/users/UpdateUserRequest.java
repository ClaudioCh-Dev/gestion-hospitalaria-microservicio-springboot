package com.hospital.auth_ms.dtos.users;

public record UpdateUserRequest(
        String username,
        String password,
        Long roleId
) {}