package com.personal.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PatientRequest(

        @NotBlank
        String documentNumber,

        @NotBlank
        String firstName,

        @NotBlank
        String lastName,

        LocalDate birthDate,

        String gender,

        String phone,

        @Email
        String email,

        String address,

        String bloodType,

        String allergies

) {}