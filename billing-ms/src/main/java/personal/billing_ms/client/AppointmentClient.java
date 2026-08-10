package personal.billing_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import personal.billing_ms.config.AppointmentFeignConfig;
import personal.billing_ms.dto.AppointmentResponse;

@FeignClient(
        name = "appointment-ms",
        configuration = AppointmentFeignConfig.class
)
public interface AppointmentClient {

    @GetMapping("/appointments/{id}")
    AppointmentResponse findById(
            @PathVariable("id") Long id
    );
}