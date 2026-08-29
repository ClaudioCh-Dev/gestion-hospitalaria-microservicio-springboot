package personal.appointment_ms.streams;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import personal.shared.event.AppointmentCreatedEvent;
import personal.shared.event.AppointmentCreatedTypeEvent;
//import personal.shared.event.AppointmentEvent;
import personal.shared.event.AppointmentUpdateStatusEvent;
import personal.appointment_ms.security.UserContext;
import personal.appointment_ms.security.UserContextHolder;

@Component
@AllArgsConstructor
public class AppointmentPublisher {

    private final StreamBridge streamBridge;

    public void publishAppointmentScheduled(
            AppointmentCreatedEvent appointmentEvent) {

        streamBridge.send(
                "appointment-created-out-0",
                buildMessage(appointmentEvent)
        );
    }
    
    public void publishAppointmentStatusUpdated(
            AppointmentUpdateStatusEvent appointmentEvent) {

        streamBridge.send(
                        "appointment-updated-status-out-0",
                        buildMessage(appointmentEvent)
                );
    }

    public void publishAppointmentCreatedType(
            AppointmentCreatedTypeEvent appointmentEvent) {

        streamBridge.send(
                        "appointment-created-type-out-0",
                        buildMessage(appointmentEvent)
                );
    }

    private <T> Message<T> buildMessage(T event) {

        UserContext context = UserContextHolder.get();

        MessageBuilder<T> builder =
                MessageBuilder.withPayload(event);

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

            Set<String> permissions = context.permissions();

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