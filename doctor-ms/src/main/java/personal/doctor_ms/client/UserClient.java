package personal.doctor_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import personal.doctor_ms.client.dto.CreateDoctorRequestClient;
import personal.doctor_ms.client.dto.UserResponse;

@FeignClient(name = "auth-ms")
public interface UserClient {
    
    @GetMapping("/users/{id}")
    UserResponse findById(@PathVariable Long id);

    @PostMapping("/doctor")
    UserResponse createDoctor(@RequestBody CreateDoctorRequestClient request);
}
