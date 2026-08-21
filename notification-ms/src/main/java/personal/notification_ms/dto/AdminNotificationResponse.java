package personal.notification_ms.dto;

import java.time.LocalDateTime;

public record AdminNotificationResponse(

        Long id,
        String patientName,
        String doctorName,
        String specialty,
        String eventType,
        String status,
        String reason,
        LocalDateTime scheduledAt,
        boolean adminRead

) {}