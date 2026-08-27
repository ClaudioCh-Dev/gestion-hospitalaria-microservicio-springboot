package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import personal.appointment_ms.exceptions.AppointmentErrorCode;
import personal.shared.exception.BusinessException;

@Configuration
public class PatientFeignConfig {

    @Bean
    public ErrorDecoder patientErrorDecoder() {

        return (methodKey, response) -> {

            return switch (response.status()) {

                case 400 -> new BusinessException(
                        AppointmentErrorCode.PATIENT_INVALID_REQUEST,
                        "Solicitud inválida en patient-ms"
                );

                case 404 -> new BusinessException(
                        AppointmentErrorCode.PATIENT_NOT_FOUND,
                        "Paciente no encontrado"
                );

                case 409 -> new BusinessException(
                        AppointmentErrorCode.PATIENT_CONFLICT,
                        "Conflicto al consultar el paciente"
                );

                case 422 -> new BusinessException(
                        AppointmentErrorCode.PATIENT_VALIDATION_ERROR,
                        "Datos del paciente inválidos"
                );

                default -> new ErrorDecoder.Default()
                        .decode(methodKey, response);
            };
        };
    }
}