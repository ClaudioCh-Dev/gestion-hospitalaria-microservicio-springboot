package com.hospital.auth_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import personal.shared.exception.BusinessException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // =========================================================
    // BUSINESS EXCEPTION
    // =========================================================

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(
            BusinessException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Authentication error path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.valueOf(ex.getStatus()),
                ex.getMessage()
        );

        problem.setProperty(
                "code",
                ex.getCode()
        );

        return problem;
    }
}