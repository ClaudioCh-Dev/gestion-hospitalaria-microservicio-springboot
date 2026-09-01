package com.hospital.auth_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import personal.shared.exception.BusinessException;
import personal.shared.exception.GenericErrorCode;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
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

    // =========================================================
    // VALIDATION ERROR
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Validation error path={}",
                request.getRequestURI()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Los datos enviados no son válidos"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.VALIDATION_ERROR
        );

        return problem;
    }

    // =========================================================
    // DATA INTEGRITY ERROR
    // =========================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityException(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {

        log.error(
                "Database integrity error path={}",
                request.getRequestURI(),
                ex
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "No se pudo completar la operación debido a una restricción de datos"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.DATA_INTEGRITY_ERROR
        );

        return problem;
    }

    // =========================================================
    // ILLEGAL ARGUMENT
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgumentException(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Invalid argument path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INVALID_ARGUMENT
        );

        return problem;
    }

    // =========================================================
    // GENERIC / UNEXPECTED ERROR
    // =========================================================

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGenericException(
            Exception ex,
            HttpServletRequest request
    ) {

        log.error(
                "Unexpected error path={}",
                request.getRequestURI(),
                ex
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INTERNAL_ERROR
        );

        return problem;
    }
}