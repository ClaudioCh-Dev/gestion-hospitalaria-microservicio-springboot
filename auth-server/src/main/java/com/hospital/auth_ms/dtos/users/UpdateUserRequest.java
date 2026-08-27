package com.hospital.auth_ms.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateUserRequest(
        
        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser válido")
        String email,

        @NotBlank(message = "La contraseña es requerida")
        String password,

        @NotNull(message = "El rol es requerido")
        Long roleId
) {}