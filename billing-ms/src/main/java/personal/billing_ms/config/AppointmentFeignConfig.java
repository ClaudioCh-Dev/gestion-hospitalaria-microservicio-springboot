package personal.billing_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;
import personal.billing_ms.exceptions.AppointmentNotFoundException;

@Configuration
public class AppointmentFeignConfig {

    @Bean
    public ErrorDecoder appointmentErrorDecoder() {
        return (methodKey, response) -> {

            if (response.status() == 404) {
                return new AppointmentNotFoundException();
            }

            return new ErrorDecoder.Default()
                    .decode(methodKey, response);
        };
    }
}