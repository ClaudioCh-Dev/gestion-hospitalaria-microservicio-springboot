package com.personal.streams;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class PatientPublisher {

    private final StreamBridge streamBridge;

    /*
     * Topic name / Binding -> patient-created
     */
    public void publishPatientCreated(Object patientData) {
        streamBridge.send("patient-created-out-0", patientData);
    }

}