package com.hospital.auth_ms.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendActivationRequest(

        @NotBlank
        @Email
        String email

) {
}