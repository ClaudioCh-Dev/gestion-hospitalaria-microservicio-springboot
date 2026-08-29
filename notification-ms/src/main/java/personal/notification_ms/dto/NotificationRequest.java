package personal.notification_ms.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Map;

import personal.notification_ms.model.NotificationType;

@Builder
public record NotificationRequest(
        NotificationType type,

        String title,

        String message,

        String referenceType,

        Long referenceId,

        Map<String, Object> metadata,

        LocalDateTime createdAt) {
}