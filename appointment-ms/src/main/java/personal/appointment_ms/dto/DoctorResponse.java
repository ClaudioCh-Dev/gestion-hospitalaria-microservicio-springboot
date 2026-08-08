package personal.appointment_ms.dto;

import java.time.LocalTime;

public record DoctorResponse(
        Long id,
        String licenseNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        Long specialtyId,
        LocalTime scheduleStart,
        LocalTime scheduleEnd,
        Boolean active
) {}