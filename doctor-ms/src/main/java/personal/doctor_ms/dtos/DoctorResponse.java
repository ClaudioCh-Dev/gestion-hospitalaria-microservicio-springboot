package personal.doctor_ms.dtos;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record DoctorResponse(
        Long id,
        String licenseNumber,
        String firstName,
        String lastName,
        String email,
        String phone,
        SpecialtyResponse specialty,
        LocalTime scheduleStart,
        LocalTime scheduleEnd,
        Boolean active,
        LocalDateTime createdAt
) {
}
