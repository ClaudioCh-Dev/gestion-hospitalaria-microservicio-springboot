package com.hospital.auth_ms.dtos.users;

public record CreateUserRequest(
        String username,
        String password,
        Long roleId
) {}