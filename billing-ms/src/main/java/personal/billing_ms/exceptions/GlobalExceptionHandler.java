package personal.billing_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import feign.FeignException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

       
        @ExceptionHandler(FeignException.class)
        public ProblemDetail handleFeignException(
                        FeignException ex,
                        HttpServletRequest request) {

                log.error(
                                "Feign communication error path={} status={}",
                                request.getRequestURI(),
                                ex.status(),
                                ex);

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.SERVICE_UNAVAILABLE,
                                "A required service is currently unavailable");

                problem.setTitle("Service unavailable");
                problem.setProperty("code", "SERVICE_UNAVAILABLE");

                return problem;
        }

        @ExceptionHandler(Exception.class)
        public ProblemDetail handleGenericException(
                        Exception ex,
                        HttpServletRequest request) {

                log.error(
                                "Unexpected error path={} message={}",
                                request.getRequestURI(),
                                ex.getMessage(),
                                ex);

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.INTERNAL_SERVER_ERROR,
                                "An unexpected error occurred");

                problem.setTitle("Internal server error");
                problem.setProperty("code", "INTERNAL_ERROR");

                return problem;
        }
}