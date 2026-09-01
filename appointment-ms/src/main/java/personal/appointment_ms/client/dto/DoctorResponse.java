package personal.appointment_ms.client.dto;

import java.time.LocalTime;

public record DoctorResponse(
        Long id,
        String licenseNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        Long userId,
        Long specialtyId,
        String specialtyName,
        LocalTime scheduleStart,
        LocalTime scheduleEnd,
        Boolean active
) {}