package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;
import personal.appointment_ms.exceptions.DoctorNotFoundException;

@Configuration
public class DoctorFeignConfig {

    @Bean
    public ErrorDecoder doctorErrorDecoder() {
        return (methodKey, response) -> {

            if (response.status() == 404) {
                return new DoctorNotFoundException();
            }

            return new ErrorDecoder.Default()
                    .decode(methodKey, response);
        };
    }
}