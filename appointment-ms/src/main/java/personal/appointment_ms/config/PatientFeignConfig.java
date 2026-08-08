package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;
import personal.appointment_ms.exceptions.PatientNotFoundException;

@Configuration
public class PatientFeignConfig {

    @Bean
    public ErrorDecoder patientErrorDecoder() {
        return (methodKey, response) -> {

            if (response.status() == 404) {
                return new PatientNotFoundException();
            }

            return new ErrorDecoder.Default()
                    .decode(methodKey, response);
        };
    }

}