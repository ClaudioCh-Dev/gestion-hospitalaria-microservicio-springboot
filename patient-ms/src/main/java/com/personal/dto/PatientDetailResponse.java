package com.personal.dto;

import com.personal.enums.BloodType;
import com.personal.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record PatientDetailResponse(

        Long id,

        String documentNumber,

        String firstName,

        String lastName,

        LocalDate birthDate,

        Gender gender,

        String phone,

        String email,

        String address,

        BloodType bloodType,

        String allergies,

        Boolean active,

        LocalDateTime createdAt,

        LocalDateTime updatedAt

) {}