package personal.appointment_ms.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import lombok.extern.slf4j.Slf4j;

import personal.appointment_ms.client.DoctorClient;
import personal.appointment_ms.exceptions.AppointmentErrorCode;

import personal.shared.exception.BusinessException;

@Slf4j
@Component
public class DoctorClientFallbackFactory
        implements FallbackFactory<DoctorClient> {

    @Override
    public DoctorClient create(Throwable cause) {

        log.warn(
            "Fallback DoctorClient. cause={}",
            cause.getClass().getSimpleName()
        );

        return id -> {

            if (cause instanceof BusinessException ex) {
                throw ex;
            }

            if (cause instanceof CallNotPermittedException) {
                throw new BusinessException(
                    AppointmentErrorCode.DOCTOR_SERVICE_UNAVAILABLE,
                    "Doctor MS temporalmente no disponible"
                );
            }

            throw new BusinessException(
                AppointmentErrorCode.DOCTOR_SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con Doctor MS"
            );
        };
    }
}