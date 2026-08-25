package personal.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import personal.gateway.exceptions.GatewayErrorCode;

@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback")
    public ResponseEntity<ProblemDetail> fallback(
            @RequestHeader(
                    value = "X-Fallback-Service",
                    defaultValue = "unknown"
            )
            String service) {

        log.warn("🔥 FALLBACK ACTIVATED: {}", service);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.SERVICE_UNAVAILABLE,
                "The requested service '" + service
                        + "' is temporarily unavailable."
        );

        problem.setProperty(
                "code",
                GatewayErrorCode.SERVICE_UNAVAILABLE.name()
        );

        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(problem);
    }
}