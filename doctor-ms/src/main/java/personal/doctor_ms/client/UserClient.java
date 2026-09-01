package personal.doctor_ms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import personal.doctor_ms.client.config.UserFeignConfig;
import personal.doctor_ms.client.dto.CreateDoctorRequestClient;
import personal.doctor_ms.client.dto.UserResponse;

@FeignClient(
        name = "auth-server",
        configuration = UserFeignConfig.class
)
public interface UserClient {

    @GetMapping("/auth-server/users/{id}")
    UserResponse findById(@PathVariable Long id);

    @PostMapping("/auth-server/users/doctor")
    UserResponse createDoctor(
            @RequestBody CreateDoctorRequestClient request
    );
}