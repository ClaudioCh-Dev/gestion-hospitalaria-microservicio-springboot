package personal.appointment_ms.client;

import org.springframework.stereotype.Component;

import personal.appointment_ms.dto.PatientResponse;
import personal.appointment_ms.exceptions.ErrorCode;
import personal.shared.exception.BusinessException;

@Component
public class PatientClientFallback implements PatientClient {

    @Override
    public PatientResponse findById(Long id) {

        throw new BusinessException(
                ErrorCode.PATIENT_SERVICE_UNAVAILABLE,
                "Patient MS no está disponible"
        );
    }
}