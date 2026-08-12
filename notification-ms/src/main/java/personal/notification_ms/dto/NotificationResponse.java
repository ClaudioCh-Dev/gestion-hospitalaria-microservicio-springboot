package personal.notification_ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record NotificationResponse(
        Long id,
        Long appointmentId,
        Long patientId,
        String patientName,
        Long doctorId,
        String doctorName,
        String specialty,
        String eventType,
        String status,
        String reason,
        LocalDateTime scheduledAt,
        BigDecimal amount,
        boolean patientRead,
        boolean doctorRead,
        LocalDateTime createdAt
) {
}