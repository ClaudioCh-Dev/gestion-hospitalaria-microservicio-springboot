package personal.medical_record_listener.listeners;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import personal.medical_record_listener.security.UserContext;
import personal.medical_record_listener.security.UserContextHolder;
import personal.medical_record_listener.service.MedicalRecordService;
import personal.shared.event.AppointmentEvent;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MedicalRecordListener {

    private final MedicalRecordService service;

    @Bean
    public Consumer<Message<AppointmentEvent>> appointmentCompletedConsumer() {

        return message -> {

            try {

                UserContext context = buildUserContext(message);

                UserContextHolder.set(context);

                AppointmentEvent event = message.getPayload();

                log.info(
                        "Appointment completed received: {}",
                        event);

                service.save(event);

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
}