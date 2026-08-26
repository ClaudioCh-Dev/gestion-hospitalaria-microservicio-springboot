package personal.notification_ms.listeners;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.security.UserContext;
import personal.notification_ms.security.UserContextHolder;
import personal.notification_ms.service.INotificationService;
import personal.notification_ms.service.SseService;
import personal.shared.event.AppointmentEvent;

@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationListener {

    private final SseService sseService;
    private final INotificationService notificationService;

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentCreatedConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message));

                AppointmentEvent event = message.getPayload();

                log.info(
                        "Notification received for appointment created: {}",
                        event);

                saveAndSend(event);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentConfirmedConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message));

                AppointmentEvent event = message.getPayload();

                log.info(
                        "Notification received for appointment confirmed: {}",
                        event);

                saveAndSend(event);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentCompletedConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message));

                AppointmentEvent event = message.getPayload();

                log.info(
                        "Notification received for appointment completed: {}",
                        event);

                saveAndSend(event);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentCanceledConsumer() {

        return message -> {

            try {

                UserContextHolder.set(
                        buildUserContext(message));

                AppointmentEvent event = message.getPayload();

                log.info(
                        "Notification received for appointment canceled: {}",
                        event);

                saveAndSend(event);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    private UserContext buildUserContext(
            Message<?> message) {

        String userId = (String) message.getHeaders()
                .get("X-User-Id");

        String role = (String) message.getHeaders()
                .get("X-Role");

        String permissionsHeader = (String) message.getHeaders()
                .get("X-Permissions");

        Set<String> permissions = permissionsHeader == null
                ? Set.of()
                : Arrays.stream(
                        permissionsHeader.split(","))
                        .map(permission -> permission.trim())
                        .collect(Collectors.toSet());

        return new UserContext(
                userId != null
                        ? Long.valueOf(userId)
                        : null,
                role,
                permissions);
    }

    private void saveAndSend(
            AppointmentEvent event) {

        NotificationRequest request = new NotificationRequest(
                event.appointmentId(),
                event.patientId(),
                event.patientName(),
                event.doctorId(),
                event.doctorName(),
                event.specialty(),
                event.eventType(),
                event.status(),
                event.reason(),
                LocalDateTime.parse(
                        event.scheduledAt()),
                event.amount());

        notificationService.save(request);

        sseService.sendNotification(event);
    }
}