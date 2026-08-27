package com.personal.streams;

import java.util.Set;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.personal.security.UserContext;
import com.personal.security.UserContextHolder;

import lombok.AllArgsConstructor;

import personal.shared.event.PatientCreateEvent;
import personal.shared.event.PatientUpdateEvent;

@Component
@AllArgsConstructor
public class PatientPublisher {

    private final StreamBridge streamBridge;

    /*
     * Topic name / Binding -> patient-created
     */
    public void publishPatientCreated(
            PatientCreateEvent patientData) {

        streamBridge.send(
                "patient-created-out-0",
                buildMessage(patientData)
        );
    }

    /*
     * Topic name / Binding -> patient-updated
     */
    public void publishPatientUpdated(
            PatientUpdateEvent patientData) {

        streamBridge.send(
                "patient-updated-out-0",
                buildMessage(patientData)
        );
    }

    private <T> Message<T> buildMessage(T event) {

        MessageBuilder<T> builder =
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