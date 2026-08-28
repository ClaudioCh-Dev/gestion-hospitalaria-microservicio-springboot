package personal.appointment_ms.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Configuration
public class BillingFeignConfig {

    @Bean
    public ErrorDecoder billingErrorDecoder() {

        return (methodKey, response) -> {

            return switch (response.status()) {

                case 400 -> new BusinessException(
                        AppointmentErrorCode.BILLING_INVALID_REQUEST,
                        "Solicitud inválida en billing-ms"
                );

                case 404 -> new BusinessException(
                        AppointmentErrorCode.BILLING_TARIFF_NOT_FOUND,
                        "Tarifa no encontrada"
                );

                case 409 -> new BusinessException(
                        AppointmentErrorCode.BILLING_CONFLICT,
                        "Conflicto en billing-ms"
                );

                case 422 -> new BusinessException(
                        AppointmentErrorCode.BILLING_VALIDATION_ERROR,
                        "Datos de la tarifa inválidos"
                );

                default -> new ErrorDecoder.Default()
                        .decode(methodKey, response);
            };
        };
    }
}