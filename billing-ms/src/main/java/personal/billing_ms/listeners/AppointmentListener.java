package personal.billing_ms.listeners;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.billing_ms.dto.AppointmentEventRequest;
import personal.billing_ms.service.IBillingRecordService;
import personal.shared.event.AppointmentEvent;
import personal.billing_ms.security.UserContext;
import personal.billing_ms.security.UserContextHolder;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class AppointmentListener {

    private final IBillingRecordService billingRecordService;

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentCompletedConsumer() {

        return message -> {

            try {

                UserContext context = buildUserContext(message);

                UserContextHolder.set(context);

                AppointmentEvent event = message.getPayload();

                var request = new AppointmentEventRequest(
                        event.appointmentId(),
                        event.patientId(),
                        event.amount());

                billingRecordService
                        .createBillingFromAppointment(request);

            } finally {

                UserContextHolder.clear();
            }
        };
    }

    private UserContext buildUserContext(Message<?> message) {

        String userId = (String) message.getHeaders()
                .get("X-User-Id");

        String role = (String) message.getHeaders()
                .get("X-Role");

        String permissionsHeader = (String) message.getHeaders()
                .get("X-Permissions");

        Set<String> permissions = permissionsHeader == null
                ? Set.of()
                : Arrays.stream(permissionsHeader.split(","))
                        .map(permission -> permission.trim())
                        .collect(Collectors.toSet());

        return new UserContext(
                userId != null
                        ? Long.valueOf(userId)
                        : null,
                role,
                permissions);
    }
}