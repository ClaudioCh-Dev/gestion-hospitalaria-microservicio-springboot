package personal.appointment_ms.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateAppointmentRequest(

        @NotNull
        Long patientId,

        @NotNull
        Long doctorId,

        @NotNull
        LocalDateTime scheduledAt,

        @Min(1)
        Integer durationMinutes,

        String reason,

        String notes
) {
}