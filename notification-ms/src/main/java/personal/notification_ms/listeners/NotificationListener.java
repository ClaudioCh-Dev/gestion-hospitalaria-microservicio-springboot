package personal.notification_ms.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import personal.notification_ms.dto.NotificationRequest;
import personal.notification_ms.service.INotificationService;
import personal.notification_ms.service.SseService;
import personal.shared.event.AppointmentEvent;

import java.time.LocalDateTime;
import java.util.function.Consumer;

@RequiredArgsConstructor
@Slf4j
@Component
public class NotificationListener {

    private final SseService sseService;
    private final INotificationService notificationService;

    @Bean
    public Consumer<AppointmentEvent> appointmentCreatedConsumer() {
        return event -> {
            log.info("Notification received for appointment created: {}", event);

            saveAndSend(event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentConfirmedConsumer() {
        return event -> {
            log.info("Notification received for appointment confirmed: {}", event);

            saveAndSend(event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentCompletedConsumer() {
        return event -> {
            log.info("Notification received for appointment completed: {}", event);

            saveAndSend(event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentCanceledConsumer() {
        return event -> {
            log.info("Notification received for appointment canceled: {}", event);

            saveAndSend(event);
        };
    }

    private void saveAndSend(AppointmentEvent event) {

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
                LocalDateTime.parse(event.scheduledAt()),
                event.amount()
        );

        notificationService.save(request);

        sseService.sendNotification(event);
    }
}