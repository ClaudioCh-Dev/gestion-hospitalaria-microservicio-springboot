package personal.notification_ms.dto;

import java.time.LocalDateTime;

public record DoctorNotificationResponse(

        Long id,
        String patientName,
        String eventType,
        String status,
        String reason,
        LocalDateTime scheduledAt,
        boolean doctorRead
) {}