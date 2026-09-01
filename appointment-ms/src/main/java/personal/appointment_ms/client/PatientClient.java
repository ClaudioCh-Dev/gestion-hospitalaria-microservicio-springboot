package personal.appointment_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import personal.appointment_ms.client.dto.PatientResponse;
import personal.appointment_ms.client.fallback.PatientClientFallbackFactory;
import personal.appointment_ms.config.PatientFeignConfig;

@FeignClient(name = "patient-ms", configuration = PatientFeignConfig.class, fallbackFactory = PatientClientFallbackFactory.class)
public interface PatientClient {

    @GetMapping(path = "/patients/crud/{id}")
    PatientResponse findById(@PathVariable("id") Long id);

}
