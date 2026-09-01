package personal.appointment_ms.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class BillingFeignConfig {

    @Bean
    public ErrorDecoder billingErrorDecoder(ObjectMapper objectMapper) {
        return new GlobalFeignErrorDecoder(objectMapper);
    }
}