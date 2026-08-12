package personal.medical_record_listener.listeners;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import personal.medical_record_listener.service.MedicalRecordService;
import personal.shared.event.AppointmentEvent;

import java.util.function.Consumer;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MedicalRecordListener {

    private final MedicalRecordService service;

    @Bean
    public Consumer<AppointmentEvent> appointmentCompletedConsumer() {
        return event -> {
            log.info("Appointment completed received: {}", event);

            service.save(event);
        };
    }
}