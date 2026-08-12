package personal.appointment_ms.client;

import org.springframework.stereotype.Component;

import personal.appointment_ms.dto.PatientResponse;
import personal.appointment_ms.exceptions.PatientServiceUnavailableException;

@Component
public class PatientClientFallback implements PatientClient {

    @Override
    public PatientResponse findById(Long id) {
        throw new PatientServiceUnavailableException(
            "Patient MS no está disponible"
        );
    }
}