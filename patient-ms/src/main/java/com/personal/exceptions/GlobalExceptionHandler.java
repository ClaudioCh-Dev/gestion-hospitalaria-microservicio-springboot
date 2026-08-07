package com.personal.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(PatientNotFoundException.class)
    public ProblemDetail handlePatientNotFound(
            PatientNotFoundException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Patient not found path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );


        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage()
        );

        problem.setTitle("Patient not found");
        problem.setProperty("code", "PATIENT_NOT_FOUND");


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