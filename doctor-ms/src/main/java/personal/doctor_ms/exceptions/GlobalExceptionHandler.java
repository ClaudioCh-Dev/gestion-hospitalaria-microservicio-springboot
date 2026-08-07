package personal.doctor_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(DoctorNotFoundException.class)
    public ProblemDetail handleDoctorNotFound(
            DoctorNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Doctor not found path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );


        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setTitle("Doctor not found");
        problem.setProperty("code", "DOCTOR_NOT_FOUND");

        return problem;
    }


    @ExceptionHandler(SpecialtyNotFoundException.class)
    public ProblemDetail handleSpecialtyNotFound(
            SpecialtyNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Specialty not found path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );


        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setTitle("Specialty not found");
        problem.setProperty("code", "SPECIALTY_NOT_FOUND");

        return problem;
    }


    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected error path={} message={}",
                request.getRequestURI(),
                ex.getMessage(),
                ex
        );


        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred"
        );

        problem.setTitle("Internal server error");
        problem.setProperty("code", "INTERNAL_ERROR");

        return problem;
    }
}