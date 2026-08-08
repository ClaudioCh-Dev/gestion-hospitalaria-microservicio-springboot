package personal.appointment_ms.client;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import personal.appointment_ms.config.DoctorFeignConfig;
import personal.appointment_ms.dto.DoctorResponse;

@FeignClient(
        name = "doctor-ms",
        configuration = DoctorFeignConfig.class
)
public interface DoctorClient {

    @GetMapping(path = "/doctors/{id}")
    Optional<DoctorResponse> findById(@PathVariable("id") Long id);
}
