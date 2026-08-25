package com.personal.dto;

import java.time.LocalDate;

import com.personal.enums.Gender;

public record PatientResponse(

        Long id,

        String documentNumber,

        String firstName,

        String lastName,
        
        Gender gender,

        LocalDate birthDate,

        String phone,

        String email,

        Boolean active

) {}