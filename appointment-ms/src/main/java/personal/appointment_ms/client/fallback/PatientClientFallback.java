package personal.appointment_ms.client.fallback;

import org.springframework.stereotype.Component;

import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.client.dto.PatientResponse;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Component
public class PatientClientFallback implements PatientClient {

    @Override
    public PatientResponse findById(Long id) {

        throw new BusinessException(
                AppointmentErrorCode.PATIENT_SERVICE_UNAVAILABLE,
                "Patient MS no está disponible"
        );
    }
}