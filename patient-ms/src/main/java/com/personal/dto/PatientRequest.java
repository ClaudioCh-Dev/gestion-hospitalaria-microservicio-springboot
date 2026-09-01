package com.personal.dto;

import com.personal.enums.BloodType;
import com.personal.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record PatientRequest(

        @NotBlank(message = "El número de documento es obligatorio")
        String documentNumber,

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        LocalDate birthDate,

        Gender gender,

        String phone,

        @Email(message = "El correo electrónico no tiene un formato válido")
        String email,

        String address,

        BloodType bloodType,

        String allergies

) {}