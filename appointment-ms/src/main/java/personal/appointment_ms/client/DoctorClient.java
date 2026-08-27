package personal.appointment_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import personal.appointment_ms.config.DoctorFeignConfig;
import personal.appointment_ms.client.fallback.DoctorClientFallback;

@FeignClient(
        name = "doctor-ms",
        configuration = DoctorFeignConfig.class,
        fallback = DoctorClientFallback.class
)
public interface DoctorClient {

    @GetMapping(path = "/doctors/crud/{id}")
    DoctorResponse findById(@PathVariable("id") Long id);
}
