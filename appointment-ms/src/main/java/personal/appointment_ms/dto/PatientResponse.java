package personal.appointment_ms.dto;

import java.time.LocalDate;

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
        Boolean active
) {}