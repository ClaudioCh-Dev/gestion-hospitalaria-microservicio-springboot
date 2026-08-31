package personal.billing_ms.listeners;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import personal.billing_ms.dto.CreateBillingTariffRequest;
import personal.billing_ms.dto.UpdateBillingTariffRequest;
import personal.billing_ms.service.IBillingRecordService;
import personal.billing_ms.service.IBillingTariffService;
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentCreatedTypeEvent;
import personal.shared.event.AppointmentEventRequest;
import personal.shared.event.AppointmentUpdateStatusEvent;
import personal.shared.event.status.StatusAppointment;
import personal.billing_ms.security.UserContext;
import personal.billing_ms.security.UserContextHolder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppointmentListener {

    private final IBillingRecordService billingRecordService;
    private final IBillingTariffService billingTariffService;

    @Bean
    public Consumer<Message<AppointmentUpdateStatusEvent>> appointmentUpdatedStatusConsumer() {
        return message -> executeWithUserContext(
                message,
                event -> {
                    if (event.status() == StatusAppointment.CANCELLED) {
                        billingRecordService.cancelBillingRecord(
                                event.appointmentId());
                    }
                });
    }

    @Bean
    public Consumer<Message<AppointmentCreatedEvent>> appointmentCreatedConsumer() {
        return message -> executeWithUserContext(
                message,
                event -> {

                    var request = new AppointmentEventRequest(
                            event.appointmentId(),
                            event.patientId(),
                            event.amount());

                    billingRecordService.createBillingFromAppointment(request);
                });
    }

    @Bean
    public Consumer<Message<AppointmentCreatedTypeEvent>> appointmentCreatedTypeConsumer() {

        return message -> executeWithUserContext(
                message,
                event -> {

                    log.info(
                            "Received appointment created type event: {}",
                            event);
                   
                    if(billingTariffService.getTariff(event.id()) != null) {
                        UpdateBillingTariffRequest request = new UpdateBillingTariffRequest(
                            BigDecimal.ZERO,
                            "PEN");
                        
                        billingTariffService.updateTariff(event.id(), request);
                        return;
                    }

                    CreateBillingTariffRequest request = new CreateBillingTariffRequest(
                            event.id(),
                            BigDecimal.ZERO,
                            "PEN");

                    billingTariffService.createTariff(request);
                });
    }

    private <T> void executeWithUserContext(
            Message<T> message,
            Consumer<T> action) {

        try {
            UserContextHolder.set(buildUserContext(message));
            action.accept(message.getPayload());

        } finally {
            UserContextHolder.clear();
        }
    }

    private UserContext buildUserContext(Message<?> message) {

        String userId = (String) message.getHeaders().get("X-User-Id");
        String role = (String) message.getHeaders().get("X-Role");
        String permissionsHeader = (String) message.getHeaders().get("X-Permissions");

        Set<String> permissions = permissionsHeader == null
                ? Set.of()
                : Arrays.stream(permissionsHeader.split(","))
                        .map(String::trim)
                        .filter(permission -> !permission.isBlank())
                        .collect(Collectors.toSet());

        return new UserContext(
                userId != null ? Long.valueOf(userId) : null,
                role,
                permissions);
    }
}