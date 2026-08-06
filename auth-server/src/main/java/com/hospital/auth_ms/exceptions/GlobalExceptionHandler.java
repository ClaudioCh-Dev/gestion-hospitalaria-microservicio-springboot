package com.hospital.auth_ms.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidCredentialsException.class)
    public ProblemDetail handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Authentication failed path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        problem.setTitle("Authentication failed");
        problem.setProperty("code", "AUTH_INVALID_CREDENTIALS");

        return problem;
    }



    @ExceptionHandler(InvalidTokenException.class)
    public ProblemDetail handleInvalidToken(
            InvalidTokenException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Invalid token path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage()
        );

        problem.setTitle("Invalid token");
        problem.setProperty("code", "AUTH_INVALID_TOKEN");

        return problem;
    }
}