package personal.appointment_ms.exceptions;

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

        @ExceptionHandler(AppointmentNotFoundException.class)
        public ProblemDetail handleAppointmentNotFound(
                        AppointmentNotFoundException ex,
                        HttpServletRequest request) {

                log.warn(
                                "Appointment not found path={} message={}",
                                request.getRequestURI(),
                                ex.getMessage());

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.NOT_FOUND,
                                ex.getMessage());

                problem.setTitle("Appointment not found");
                problem.setProperty("code", "APPOINTMENT_NOT_FOUND");

                return problem;
        }

        @ExceptionHandler(InvalidAppointmentStatusException.class)
        public ProblemDetail handleInvalidAppointmentStatus(
                        InvalidAppointmentStatusException ex,
                        HttpServletRequest request) {

                log.warn(
                                "Invalid appointment status path={} message={}",
                                request.getRequestURI(),
                                ex.getMessage());

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.BAD_REQUEST,
                                ex.getMessage());

                problem.setTitle("Invalid appointment status");
                problem.setProperty("code", "INVALID_APPOINTMENT_STATUS");

                return problem;
        }

        @ExceptionHandler(AppointmentConflictException.class)
        public ProblemDetail handleAppointmentConflict(
                        AppointmentConflictException ex,
                        HttpServletRequest request) {

                log.warn(
                                "Appointment conflict path={} message={}",
                                request.getRequestURI(),
                                ex.getMessage());

                ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                                HttpStatus.CONFLICT,
                                ex.getMessage());

                problem.setTitle("Appointment conflict");
                problem.setProperty("code", "APPOINTMENT_CONFLICT");

                return problem;
        }

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