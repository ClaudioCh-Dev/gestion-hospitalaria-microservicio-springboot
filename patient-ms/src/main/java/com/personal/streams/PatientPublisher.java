package com.personal.streams;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;
import personal.shared.event.PatientCreatedEvent;

@Component
@AllArgsConstructor
public class PatientPublisher {

    private final StreamBridge streamBridge;

    /*
     * Topic name / Binding -> patient-created
     */
    public void publishPatientCreated(PatientCreatedEvent patientData) {
        streamBridge.send("patient-created-out-0", patientData);
    }

}