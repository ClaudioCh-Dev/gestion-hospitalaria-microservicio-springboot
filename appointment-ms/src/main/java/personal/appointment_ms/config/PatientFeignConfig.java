package personal.appointment_ms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class PatientFeignConfig {

    @Bean
    public ErrorDecoder patientErrorDecoder(ObjectMapper objectMapper) {
        return new GlobalFeignErrorDecoder(objectMapper);
    }
}