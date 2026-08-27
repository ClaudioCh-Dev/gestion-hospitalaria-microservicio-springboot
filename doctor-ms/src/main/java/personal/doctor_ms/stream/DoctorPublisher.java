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
import personal.shared.event.DoctorUpdateEvent;

@Component
@AllArgsConstructor
public class DoctorPublisher {

    private final StreamBridge streamBridge;

    public void publishDoctorCreated(
            DoctorCreatedEvent doctorEvent) {

        streamBridge.send(
                "doctor-created-out-0",
                buildCreatedMessage(doctorEvent)
        );
    }

    public void publishDoctorUpdated(
            DoctorUpdateEvent doctorEvent) {

        streamBridge.send(
                "doctor-updated-out-0",
                buildUpdatedMessage(doctorEvent)
        );
    }

    private Message<DoctorCreatedEvent> buildCreatedMessage(
            DoctorCreatedEvent event) {

        MessageBuilder<DoctorCreatedEvent> builder =
                MessageBuilder.withPayload(event);

        addUserContextHeaders(builder);

        return builder.build();
    }

    private Message<DoctorUpdateEvent> buildUpdatedMessage(
            DoctorUpdateEvent event) {

        MessageBuilder<DoctorUpdateEvent> builder =
                MessageBuilder.withPayload(event);

        addUserContextHeaders(builder);

        return builder.build();
    }

    private void addUserContextHeaders(
            MessageBuilder<?> builder) {

        UserContext context =
                UserContextHolder.get();

        if (context == null) {
            return;
        }

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
}