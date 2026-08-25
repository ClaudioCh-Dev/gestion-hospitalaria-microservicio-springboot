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

            if (response.status() == 404) {
                return new BusinessException(
                        AppointmentErrorCode.DOCTOR_NOT_FOUND, 
                        "Doctor no encontrado"
                );
            }

            return new ErrorDecoder.Default()
                    .decode(methodKey, response);
        };
    }
}