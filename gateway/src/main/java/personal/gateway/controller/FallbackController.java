package personal.gateway.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class FallbackController {

    @RequestMapping("/fallback")
    public ProblemDetail fallback(@RequestParam String service) {

        log.warn("Fallback activated for service: {}", service);

        ProblemDetail problem = ProblemDetail.forStatus(
                HttpStatus.SERVICE_UNAVAILABLE);

        problem.setTitle("Service Unavailable");
        problem.setDetail(
                "The requested service '" + service
                        + "' is temporarily unavailable. Please try again later.");

        problem.setProperty("timestamp", LocalDateTime.now());

        return problem;
    }
}