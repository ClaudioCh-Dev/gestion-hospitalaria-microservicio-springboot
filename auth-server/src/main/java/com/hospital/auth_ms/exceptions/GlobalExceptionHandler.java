package com.hospital.auth_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import personal.shared.exception.BusinessException;
import personal.shared.exception.GenericErrorCode;

import java.nio.file.AccessDeniedException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

        HttpStatus status = HttpStatus.resolve(ex.getStatus());
        if (status == null) {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status,
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
                GenericErrorCode.VALIDATION_ERROR.name()
        );

        return problem;
    }

    // =========================================================
    // 400 - JSON MAL FORMADO
    // =========================================================

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleInvalidRequestBody(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Invalid request body path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no tiene un formato válido"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INVALID_REQUEST_BODY.name()
        );

        return problem;
    }

    // =========================================================
    // 400 - TIPO DE PARAMETRO INCORRECTO
    // =========================================================

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleTypeMismatch(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Parameter type mismatch path={} parameter={}",
                request.getRequestURI(),
                ex.getName()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un formato inválido"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INVALID_PARAMETER.name()
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
                GenericErrorCode.DATA_INTEGRITY_ERROR.name()
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
                GenericErrorCode.INVALID_ARGUMENT.name()
        );

        return problem;
    }

    // =========================================================
    // 403 - ACCESS DENIED
    // =========================================================

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {

        log.warn(
                "Access denied path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN,
                "No tienes permisos para realizar esta operación"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.ACCESS_DENIED.name()
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
                GenericErrorCode.INTERNAL_ERROR.name()
        );

        return problem;
    }
}