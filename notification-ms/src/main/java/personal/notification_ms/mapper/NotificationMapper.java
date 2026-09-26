package personal.notification_ms.mapper;

import java.time.LocalDateTime;
import java.util.Map;

import org.mapstruct.Mapper;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationResponse toResponse(Notification notification, boolean read);

    /**
     * Los datos de la cita llegan en el metadata (Map) del request: se copian a columnas
     * para poder filtrar por médico y mostrarlos en la campana.
     */
    default Notification toEntity(NotificationRequest request) {

        Map<String, Object> metadata = request.metadata() != null ? request.metadata() : Map.of();

        return Notification.builder()
                .type(request.type() != null ? request.type().name() : null)
                .title(request.title())
                .message(request.message())
                .referenceType(request.referenceType())
                .referenceId(request.referenceId())
                .doctorId(asLong(metadata.get("doctorId")))
                .doctorUserId(asLong(metadata.get("doctorUserId")))
                .patientName(asString(metadata.get("patientName")))
                .doctorName(asString(metadata.get("doctorName")))
                .specialty(asString(metadata.get("specialty")))
                .reason(asString(metadata.get("reason")))
                .appointmentStatus(asString(metadata.get("status")))
                .scheduledAt(asDateTime(metadata.get("scheduledAt")))
                .createdAt(request.createdAt())
                .build();
    }

    // Desde Kafka llegan como Long/String; desde el POST (JSON) los números llegan como Integer
    private static Long asLong(Object value) {
        if (value instanceof Number number) {
            return number.longValue();
        }
        return value != null ? Long.valueOf(value.toString()) : null;
    }

    private static String asString(Object value) {
        return value != null ? value.toString() : null;
    }

    // scheduledAt viaja como texto ISO (LocalDateTime.toString())
    private static LocalDateTime asDateTime(Object value) {
        return value != null ? LocalDateTime.parse(value.toString()) : null;
    }
}
