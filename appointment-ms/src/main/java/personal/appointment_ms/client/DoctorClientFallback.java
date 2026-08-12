package personal.appointment_ms.client;

import org.springframework.stereotype.Component;

import personal.appointment_ms.dto.DoctorResponse;
import personal.appointment_ms.exceptions.DoctorServiceUnavailableException;

@Component
class DoctorClientFallback implements DoctorClient {

    @Override
    public DoctorResponse findById(Long id) {

        throw new DoctorServiceUnavailableException(
            "Doctor MS no está disponible"
        );
    }
}