package personal.appointment_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import personal.appointment_ms.client.dto.BillingTariffResponse;
import personal.appointment_ms.client.fallback.BillingClientFallback;
import personal.appointment_ms.config.BillingFeignConfig;

@FeignClient(
        name = "billing-ms",
        configuration = BillingFeignConfig.class,
        fallback = BillingClientFallback.class
)
public interface BillingClient {

    @GetMapping("/tariffs/{appointmentTypeId}")
    BillingTariffResponse findTariffByAppointmentTypeId(
            @PathVariable("appointmentTypeId") Long appointmentTypeId
    );
}