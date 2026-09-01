package com.hospital.auth_ms.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateDoctorRequest(

        @NotBlank(message = "El email es requerido")
        @Email(message = "El email debe ser válido")
        String email
) {
}