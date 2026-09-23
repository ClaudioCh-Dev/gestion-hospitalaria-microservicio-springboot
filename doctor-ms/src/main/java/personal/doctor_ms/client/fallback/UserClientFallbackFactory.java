package personal.doctor_ms.client.fallback;

import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;

import lombok.extern.slf4j.Slf4j;

import personal.doctor_ms.client.UserClient;
import personal.doctor_ms.client.dto.CreateDoctorRequestClient;
import personal.doctor_ms.client.dto.UserResponse;
import personal.doctor_ms.exceptions.DoctorErrorCode;

import personal.shared.exception.BusinessException;

@Slf4j
@Component
public class UserClientFallbackFactory
        implements FallbackFactory<UserClient> {

    @Override
    public UserClient create(Throwable cause) {

        log.warn(
            "Fallback UserClient. cause={} message={}",
            cause.getClass().getName(),
            cause.getMessage(),
            cause
        );

        return new UserClient() {

            @Override
            public UserResponse findById(Long id) {
                throw resolve(cause);
            }

            @Override
            public UserResponse createDoctor(CreateDoctorRequestClient request) {
                throw resolve(cause);
            }
        };
    }

    private BusinessException resolve(Throwable cause) {

        if (cause instanceof BusinessException ex) {
            return ex;
        }

        if (cause instanceof CallNotPermittedException) {
            return new BusinessException(
                DoctorErrorCode.AUTH_SERVICE_UNAVAILABLE,
                "Auth Server temporalmente no disponible"
            );
        }

        if (cause instanceof java.util.concurrent.TimeoutException) {
            return new BusinessException(
                DoctorErrorCode.AUTH_SERVICE_UNAVAILABLE,
                "Auth Server no respondió a tiempo"
            );
        }

        return new BusinessException(
            DoctorErrorCode.AUTH_SERVICE_UNAVAILABLE,
            "No fue posible comunicarse con Auth Server"
        );
    }
}
