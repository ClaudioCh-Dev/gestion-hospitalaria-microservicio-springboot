package personal.billing_ms.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import lombok.extern.slf4j.Slf4j;

import personal.billing_ms.client.AppointmentClient;
import personal.billing_ms.exceptions.BillingErrorCode;

import personal.shared.exception.BusinessException;

@Slf4j
@Component
public class AppointmentClientFallbackFactory
        implements FallbackFactory<AppointmentClient> {

    @Override
    public AppointmentClient create(Throwable cause) {

        log.warn(
            "Fallback AppointmentClient. cause={} message={}",
            cause.getClass().getName(),
            cause.getMessage(),
            cause
        );

        return id -> {

            if (cause instanceof BusinessException ex) {
                throw ex;
            }

            if (cause instanceof CallNotPermittedException) {
                throw new BusinessException(
                    BillingErrorCode.APPOINTMENT_SERVICE_UNAVAILABLE,
                    "Appointment MS temporalmente no disponible"
                );
            }

            if (cause instanceof java.util.concurrent.TimeoutException) {
                throw new BusinessException(
                    BillingErrorCode.APPOINTMENT_SERVICE_UNAVAILABLE,
                    "Appointment MS no respondió a tiempo"
                );
            }

            throw new BusinessException(
                BillingErrorCode.APPOINTMENT_SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con Appointment MS"
            );
        };
    }
}
