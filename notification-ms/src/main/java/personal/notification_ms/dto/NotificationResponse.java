package personal.notification_ms.dto;

import java.time.LocalDateTime;

/**
 * Notificación tal como la ve el usuario autenticado. Es la misma forma para el médico
 * (GET /crud/me), el admin (GET /crud/admin) y el stream SSE.
 */
public record NotificationResponse(
        Long id,
        String type,
        String title,
        String message,
        String referenceType,
        Long referenceId,
        Long doctorId,
        String patientName,
        String doctorName,
        String specialty,
        String appointmentStatus,
        String reason,
        LocalDateTime scheduledAt,
        // Leída por el usuario del token (notification_recipients); en el stream siempre false
        boolean read,
        LocalDateTime createdAt
) {
}
