package personal.billing_ms.streams;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;
import personal.shared.event.AppointmentEvent;

@Configuration
@Slf4j
public class AppointmentListener {

    @Bean
    public Consumer<AppointmentEvent> appointmentCompletedConsumer() {
        return event -> {
            log.info("Appointment completed: {}", event);
            
            // Aquí haces lo que necesite Billing
        };
    }
}