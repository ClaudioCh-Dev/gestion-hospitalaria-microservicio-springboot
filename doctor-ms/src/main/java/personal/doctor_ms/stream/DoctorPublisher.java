package personal.doctor_ms.stream;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import personal.doctor_ms.security.UserContext;
import personal.doctor_ms.security.UserContextHolder;
import personal.shared.event.DoctorCreatedEvent;

@Component
@AllArgsConstructor
public class DoctorPublisher {

    private final StreamBridge streamBridge;

    public void publishDoctorCreated(
            DoctorCreatedEvent doctorEvent) {

        streamBridge.send(
                "doctor-created-out-0",
                buildMessage(doctorEvent)
        );
    }

    private Message<DoctorCreatedEvent> buildMessage(
            DoctorCreatedEvent event) {

        UserContext context =
                UserContextHolder.get();

        MessageBuilder<DoctorCreatedEvent> builder =
                MessageBuilder
                        .withPayload(event);

        if (context != null) {

            if (context.userId() != null) {
                builder.setHeader(
                        "X-User-Id",
                        context.userId().toString()
                );
            }

            if (context.role() != null) {
                builder.setHeader(
                        "X-Role",
                        context.role()
                );
            }

            Set<String> permissions =
                    context.permissions();

            if (permissions != null && !permissions.isEmpty()) {
                builder.setHeader(
                        "X-Permissions",
                        String.join(",", permissions)
                );
            }
        }

        return builder.build();
    }
}