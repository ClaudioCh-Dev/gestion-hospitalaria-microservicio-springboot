package personal.appointment_ms.dto;

import java.time.LocalDateTime;

import personal.appointment_ms.entities.AppointmentStatus;

public record AppointmentResponse(

        Long id,

        Long patientId,

        Long doctorId,

        LocalDateTime scheduledAt,

        Integer durationMinutes,

        String reason,

        AppointmentStatus status,

        String notes,

        LocalDateTime createdAt

) {
}