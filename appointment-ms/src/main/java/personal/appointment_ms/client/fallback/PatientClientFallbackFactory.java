package personal.appointment_ms.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import personal.appointment_ms.client.PatientClient;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PatientClientFallbackFactory
        implements FallbackFactory<PatientClient> {

    @Override
    public PatientClient create(Throwable cause) {

        log.warn(
            "Fallback PatientClient. cause={}",
            cause.getClass().getSimpleName()
        );

        return id -> {

            if (cause instanceof BusinessException ex) {
                throw ex;
            }

            if (cause instanceof CallNotPermittedException) {
                throw new BusinessException(
                    AppointmentErrorCode.PATIENT_SERVICE_UNAVAILABLE,
                    "Patient MS temporalmente no disponible"
                );
            }

            throw new BusinessException(
                AppointmentErrorCode.PATIENT_SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con Patient MS"
            );
        };
    }
}