package personal.billing_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.ErrorDecoder;

@Configuration
public class AppointmentFeignConfig {

    @Bean
    public ErrorDecoder appointmentErrorDecoder(ObjectMapper objectMapper) {
        return new GlobalFeignErrorDecoder(objectMapper);
    }
}