package personal.notification_ms.listeners;

import java.time.LocalDateTime;
import java.util.Arrays;
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

                notificationService.save(request);

                sseService.sendNotification(request);

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

                NotificationRequest request =
                        buildAppointmentStatusNotification(event);

                notificationService.save(request);

                sseService.sendNotification(request);

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

                .metadata(Map.of(
                        "patientId", event.patientId(),
                        "patientName", event.patientName(),
                        "doctorId", event.doctorId(),
                        "doctorName", event.doctorName(),
                        "specialty", event.specialty(),
                        "scheduledAt", event.scheduledAt().toString(),
                        "reason", event.reason(),
                        "status", event.status().name(),
                        "amount", event.amount(),
                        "currency", event.currency()
                ))

                .createdAt(LocalDateTime.now())

                .build();
    }

    // ============================================================
    // APPOINTMENT STATUS NOTIFICATION
    // ============================================================

    private NotificationRequest buildAppointmentStatusNotification(
            AppointmentUpdateStatusEvent event) {

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

        String message = switch (event.status()) {

            case SCHEDULED ->
                    "La cita ha sido programada.";

            case CONFIRMED ->
                    "La cita ha sido confirmada.";

            case COMPLETED ->
                    "La cita ha sido completada.";

            case CANCELLED ->
                    "La cita ha sido cancelada.";
        };

        return NotificationRequest.builder()

                .type(type)

                .title(title)

                .message(message)

                .referenceType("APPOINTMENT")

                .referenceId(event.appointmentId())

                .metadata(Map.of(
                        "status", event.status().name()
                ))

                .createdAt(LocalDateTime.now())

                .build();
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