package personal.billing_ms.listeners;

import java.util.function.Consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import personal.billing_ms.service.IBillingRecordService;
import personal.shared.event.AppointmentEvent;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppointmentListener {

    private final IBillingRecordService billingRecordService;

    @Bean
    public Consumer<AppointmentEvent> appointmentCompletedConsumer() {
        return event -> {
            log.info("Appointment completed: {}", event);
            
            // Convertir AppointmentEvent a AppointmentEventRequest
            var request = new personal.billing_ms.dto.AppointmentEventRequest(
                event.appointmentId(),
                event.patientId(),
                event.amount()
            );
            
            billingRecordService.createBillingFromAppointment(request);
        };
    }
}