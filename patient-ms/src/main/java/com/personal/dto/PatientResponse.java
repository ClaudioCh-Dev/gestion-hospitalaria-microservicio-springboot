package com.personal.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientResponse(

        Long id,

        String documentNumber,

        String firstName,

        String lastName,

        LocalDate birthDate,

        String gender,

        String phone,

        String email,

        String address,

        String bloodType,

        String allergies,

        Boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {}