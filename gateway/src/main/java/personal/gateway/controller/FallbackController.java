package personal.gateway.controller;

import lombok.extern.slf4j.Slf4j;

import personal.gateway.exceptions.GatewayErrorCode;
import personal.shared.exception.BusinessException;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback")
    public void fallback(
            @RequestHeader("X-Fallback-Service") String service) {

        log.warn("🔥 FALLBACK ACTIVATED: {}", service);

        throw new BusinessException(
                GatewayErrorCode.SERVICE_UNAVAILABLE,
                "The requested service '" + service
                        + "' is temporarily unavailable."
        );
    }
}