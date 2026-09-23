package personal.appointment_ms.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import lombok.extern.slf4j.Slf4j;

import personal.appointment_ms.client.BillingClient;
import personal.appointment_ms.exceptions.AppointmentErrorCode;

import personal.shared.exception.BusinessException;

@Slf4j
@Component
public class BillingClientFallbackFactory
        implements FallbackFactory<BillingClient> {

    @Override
    public BillingClient create(Throwable cause) {

       log.warn(
            "Fallback BillingClient. cause={} message={}",
            cause.getClass().getName(),
            cause.getMessage(),
            cause
        );

        return appointmentTypeId -> {

            if (cause instanceof BusinessException ex) {
                throw ex;
            }

            if (cause instanceof CallNotPermittedException) {
                throw new BusinessException(
                    AppointmentErrorCode.BILLING_SERVICE_UNAVAILABLE,
                    "Billing MS temporalmente no disponible"
                );
            }

            if (cause instanceof java.util.concurrent.TimeoutException) {
                throw new BusinessException(
                    AppointmentErrorCode.BILLING_SERVICE_UNAVAILABLE,
                    "Billing MS no respondió a tiempo"
                );
            }

            throw new BusinessException(
                AppointmentErrorCode.BILLING_SERVICE_UNAVAILABLE,
                "No fue posible comunicarse con Billing MS"
            );
        };
    }
}