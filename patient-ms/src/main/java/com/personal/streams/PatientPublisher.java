package com.personal.streams;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.personal.security.UserContext;
import com.personal.security.UserContextHolder;

import lombok.AllArgsConstructor;

import personal.shared.event.PatientCreatedEvent;

@Component
@AllArgsConstructor
public class PatientPublisher {

    private final StreamBridge streamBridge;

    /*
     * Topic name / Binding -> patient-created
     */
    public void publishPatientCreated(
            PatientCreatedEvent patientData) {

        streamBridge.send(
                "patient-created-out-0",
                buildMessage(patientData)
        );
    }

    private Message<PatientCreatedEvent> buildMessage(
            PatientCreatedEvent event) {

        UserContext context =
                UserContextHolder.get();

        MessageBuilder<PatientCreatedEvent> builder =
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