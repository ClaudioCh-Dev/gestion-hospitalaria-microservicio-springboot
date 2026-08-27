package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Configuration
public class DoctorFeignConfig {

    @Bean
    public ErrorDecoder doctorErrorDecoder() {

        return (methodKey, response) -> {

            return switch (response.status()) {

                case 400 -> new BusinessException(
                        AppointmentErrorCode.DOCTOR_INVALID_REQUEST,
                        "Solicitud inválida en doctor-ms"
                );

                case 404 -> new BusinessException(
                        AppointmentErrorCode.DOCTOR_NOT_FOUND,
                        "Doctor no encontrado"
                );

                case 409 -> new BusinessException(
                        AppointmentErrorCode.DOCTOR_CONFLICT,
                        "Conflicto al consultar el doctor"
                );

                case 422 -> new BusinessException(
                        AppointmentErrorCode.DOCTOR_VALIDATION_ERROR,
                        "Datos del doctor inválidos"
                );

                default -> new ErrorDecoder.Default()
                        .decode(methodKey, response);
            };
        };
    }
}