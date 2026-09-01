package personal.doctor_ms.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;

import feign.codec.ErrorDecoder;

@Configuration
public class UserFeignConfig {

    @Bean
    public ErrorDecoder userErrorDecoder(ObjectMapper objectMapper) {
        return new GlobalFeignErrorDecoder(objectMapper);
    }
}