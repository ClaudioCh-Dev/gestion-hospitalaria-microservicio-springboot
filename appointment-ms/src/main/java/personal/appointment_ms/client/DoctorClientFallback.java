package personal.appointment_ms.client;

import org.springframework.stereotype.Component;

import personal.appointment_ms.dto.DoctorResponse;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Component
class DoctorClientFallback implements DoctorClient {

    @Override
    public DoctorResponse findById(Long id) {

        throw new BusinessException(
                AppointmentErrorCode.DOCTOR_SERVICE_UNAVAILABLE,
                "Doctor MS no está disponible"
        );
    }
}