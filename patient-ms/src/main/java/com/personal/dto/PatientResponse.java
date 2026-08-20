package com.personal.dto;

import java.time.LocalDate;

public record PatientResponse(

        Long id,

        String documentNumber,

        String firstName,

        String lastName,

        LocalDate birthDate,

        String phone,

        String email,

        Boolean active
) {}
