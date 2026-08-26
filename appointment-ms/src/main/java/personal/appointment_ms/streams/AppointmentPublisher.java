package personal.appointment_ms.streams;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

import personal.shared.event.AppointmentEvent;
import personal.appointment_ms.security.UserContext;
import personal.appointment_ms.security.UserContextHolder;

@Component
@AllArgsConstructor
public class AppointmentPublisher {

    private final StreamBridge streamBridge;

    public void publishAppointmentScheduled(
            AppointmentEvent appointmentEvent) {

        streamBridge.send(
                "appointment-created-out-0",
                buildMessage(appointmentEvent)
        );
    }

    public void publishAppointmentConfirmed(
            AppointmentEvent appointmentEvent) {

        streamBridge.send(
                "appointment-confirmed-out-0",
                buildMessage(appointmentEvent)
        );
    }

    public void publishAppointmentCompleted(
            AppointmentEvent appointmentEvent) {

        streamBridge.send(
                "appointment-completed-out-0",
                buildMessage(appointmentEvent)
        );
    }

    public void publishAppointmentCanceled(
            AppointmentEvent appointmentEvent) {

        streamBridge.send(
                "appointment-canceled-out-0",
                buildMessage(appointmentEvent)
        );
    }

    private Message<AppointmentEvent> buildMessage(
            AppointmentEvent event) {

        UserContext context =
                UserContextHolder.get();

        MessageBuilder<AppointmentEvent> builder =
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