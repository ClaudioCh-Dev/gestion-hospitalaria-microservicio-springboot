package personal.notification_ms.dto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record NotificationRequest(
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
        BigDecimal amount
) {
}