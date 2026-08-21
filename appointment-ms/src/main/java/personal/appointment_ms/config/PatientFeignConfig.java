package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import personal.appointment_ms.exceptions.ErrorCode;
import personal.shared.exception.BusinessException;

@Configuration
public class PatientFeignConfig {

    @Bean
    public ErrorDecoder patientErrorDecoder() {

        return (methodKey, response) -> {

            if (response.status() == 404) {
                return new BusinessException(
                        ErrorCode.PATIENT_NOT_FOUND,
                        "Paciente no encontrado"
                );
            }

            return new ErrorDecoder.Default()
                    .decode(methodKey, response);
        };
    }
}