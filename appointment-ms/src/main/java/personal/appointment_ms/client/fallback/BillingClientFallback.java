package personal.appointment_ms.client.fallback;

import org.springframework.stereotype.Component;

import personal.appointment_ms.client.BillingClient;
import personal.appointment_ms.client.dto.BillingTariffResponse;
import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Component
public class BillingClientFallback implements BillingClient {

    @Override
    public BillingTariffResponse findTariffByAppointmentTypeId(Long appointmentTypeId) {

        throw new BusinessException(
                AppointmentErrorCode.BILLING_SERVICE_UNAVAILABLE,
                "Billing MS no está disponible"
        );
    }
}