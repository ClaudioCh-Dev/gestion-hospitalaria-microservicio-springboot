package com.hospital.auth_ms.dtos.users;

public record UserResponse(
        Long id,
        String email,
        Long roleId,
        String role,
        boolean active,
        // Inactivo y con un correo de activación enviado que aún no se usa
        boolean activationPending
) {}