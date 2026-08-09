package personal.billing_ms.dto;

import java.time.LocalDateTime;

public record AppointmentResponse(
    Long id,
    Long patientId,
    Long doctorId,
    LocalDateTime scheduledAt,
    Integer durationMinutes,
    String reason,
    String status,
    String notes,
    LocalDateTime createdAt
)  {
    
}

