package personal.notification_ms.listeners;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.dto.NotificationResponse;
import personal.notification_ms.model.NotificationType;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.notification_ms.service.INotificationService;
import personal.notification_ms.service.ISseService;
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentUpdateStatusEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final ISseService sseService;
    private final INotificationService notificationService;

    // ============================================================
    // APPOINTMENT CREATED
    // ============================================================

    @Bean
    public Consumer<Message<AppointmentCreatedEvent>>
            appointmentCreatedConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message)
                );

                AppointmentCreatedEvent event =
                        message.getPayload();

                log.info(
                        "Notification received for appointment created: {}",
                        event
                );

                NotificationRequest request =
                        buildAppointmentCreatedNotification(event);

                // Se envía la guardada: el frontend recibe el id para marcarla como leída
                NotificationResponse saved =
                        notificationService.save(request);

                // Solo a los admins y al médico de la cita
                sseService.sendNotification(event.doctorUserId(), saved);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    // ============================================================
    // APPOINTMENT STATUS UPDATED
    // ============================================================

    @Bean
    public Consumer<Message<AppointmentUpdateStatusEvent>>
            appointmentUpdateStatusConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message)
                );

                AppointmentUpdateStatusEvent event =
                        message.getPayload();

                log.info(
                        "Notification received for appointment status update: {}",
                        event
                );

                Map<String, Object> metadata =
                        buildStatusMetadata(event);

                NotificationRequest request =
                        buildAppointmentStatusNotification(event, metadata);

                NotificationResponse saved =
                        notificationService.save(request);

                // Solo a los admins y al médico de la cita
                sseService.sendNotification(
                        (Long) metadata.get("doctorUserId"),
                        saved
                );

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    // ============================================================
    // APPOINTMENT CREATED NOTIFICATION
    // ============================================================

    private NotificationRequest buildAppointmentCreatedNotification(
            AppointmentCreatedEvent event) {

        // HashMap y no Map.of: Map.of falla con valores null (p. ej. reason vacío)
        Map<String, Object> metadata = new HashMap<>();

        putIfPresent(metadata, "patientId", event.patientId());
        putIfPresent(metadata, "patientName", event.patientName());
        putIfPresent(metadata, "doctorId", event.doctorId());
        putIfPresent(metadata, "doctorUserId", event.doctorUserId());
        putIfPresent(metadata, "doctorName", event.doctorName());
        putIfPresent(metadata, "specialty", event.specialty());
        putIfPresent(metadata, "scheduledAt",
                event.scheduledAt() != null ? event.scheduledAt().toString() : null);
        putIfPresent(metadata, "reason", event.reason());
        putIfPresent(metadata, "status",
                event.status() != null ? event.status().name() : null);
        putIfPresent(metadata, "amount", event.amount());
        putIfPresent(metadata, "currency", event.currency());

        return NotificationRequest.builder()

                .type(NotificationType.APPOINTMENT_SCHEDULED)

                .title("Cita programada")

                .message(
                        "Se ha creado una nueva cita para "
                                + event.patientName()
                                + " con el doctor "
                                + event.doctorName()
                )

                .referenceType("APPOINTMENT")

                .referenceId(event.appointmentId())

                .metadata(metadata)

                .createdAt(LocalDateTime.now())

                .build();
    }

    // ============================================================
    // APPOINTMENT STATUS NOTIFICATION
    // ============================================================

    private NotificationRequest buildAppointmentStatusNotification(
            AppointmentUpdateStatusEvent event,
            Map<String, Object> metadata) {

        NotificationType type = switch (event.status()) {

            case SCHEDULED ->
                    NotificationType.APPOINTMENT_SCHEDULED;

            case CONFIRMED ->
                    NotificationType.APPOINTMENT_CONFIRMED;

            case COMPLETED ->
                    NotificationType.APPOINTMENT_COMPLETED;

            case CANCELLED ->
                    NotificationType.APPOINTMENT_CANCELLED;
        };

        String title = switch (event.status()) {

            case SCHEDULED ->
                    "Cita programada";

            case CONFIRMED ->
                    "Cita confirmada";

            case COMPLETED ->
                    "Cita completada";

            case CANCELLED ->
                    "Cita cancelada";
        };

        String action = switch (event.status()) {

            case SCHEDULED ->
                    "programada";

            case CONFIRMED ->
                    "confirmada";

            case COMPLETED ->
                    "completada";

            case CANCELLED ->
                    "cancelada";
        };

        // "La cita de María Gonzales ha sido confirmada." si se conoce el paciente
        Object patientName = metadata.get("patientName");

        String message = patientName != null
                ? "La cita de " + patientName + " ha sido " + action + "."
                : "La cita ha sido " + action + ".";

        return NotificationRequest.builder()

                .type(type)

                .title(title)

                .message(message)

                .referenceType("APPOINTMENT")

                .referenceId(event.appointmentId())

                .metadata(metadata)

                .createdAt(LocalDateTime.now())

                .build();
    }

    /**
     * El evento de estado solo trae appointmentId, status y el médico: el resto
     * (paciente, fecha...) se copia de la notificación con la que se creó la cita.
     * Los valores del evento tienen prioridad; si faltan (eventos publicados antes de
     * añadir doctorId/doctorUserId), se usan los guardados.
     */
    private Map<String, Object> buildStatusMetadata(
            AppointmentUpdateStatusEvent event) {

        Map<String, Object> metadata = new HashMap<>(
                notificationService.findAppointmentMetadata(event.appointmentId())
        );

        metadata.put("status", event.status().name());

        putIfPresent(metadata, "doctorId", event.doctorId());
        putIfPresent(metadata, "doctorUserId", event.doctorUserId());

        return metadata;
    }

    private static void putIfPresent(
            Map<String, Object> map,
            String key,
            Object value) {

        if (value != null) {
            map.put(key, value);
        }
    }

    // ============================================================
    // USER CONTEXT
    // ============================================================

    private UserContext buildUserContext(
            Message<?> message) {

        String userId =
                (String) message.getHeaders()
                        .get("X-User-Id");

        String role =
                (String) message.getHeaders()
                        .get("X-Role");

        String permissionsHeader =
                (String) message.getHeaders()
                        .get("X-Permissions");

        Set<String> permissions =
                permissionsHeader == null
                        ? Set.of()
                        : Arrays.stream(
                                permissionsHeader.split(",")
                        )
                        .map(String::trim)
                        .collect(Collectors.toSet());

        return new UserContext(

                userId != null
                        ? Long.valueOf(userId)
                        : null,

                role,

                permissions
        );
    }
}