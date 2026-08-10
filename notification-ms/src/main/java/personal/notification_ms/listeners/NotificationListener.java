package personal.notification_ms.listeners;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import personal.shared.event.AppointmentEvent;
import personal.shared.event.PatientCreatedEvent;

import java.util.function.Consumer;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class NotificationListener {

    @Bean
    public Consumer<AppointmentEvent> appointmentCreatedConsumer() {
        return event -> {
            System.out.println(event);
            log.info("Notification received for appointment created: {}", event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentConfirmedConsumer() {
        return event -> {
            System.out.println(event);
            log.info("Notification received for appointment confirmed: {}", event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentCompletedConsumer() {
        return event -> {
            System.out.println(event);
            log.info("Notification received for appointment completed: {}", event);
        };
    }

    @Bean
    public Consumer<AppointmentEvent> appointmentCanceledConsumer() {
        return event -> {
            System.out.println(event);
            log.info("Notification received for appointment canceled: {}", event);
        };
    }

    @Bean
    public Consumer<PatientCreatedEvent> patientCreatedConsumer() {
        return event -> {
            System.out.println(event);
            log.info("Notification received for patient created: {}", event);
        };
    }
}
