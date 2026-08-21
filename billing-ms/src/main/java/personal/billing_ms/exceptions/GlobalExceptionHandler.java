package personal.billing_ms.exceptions;

import jakarta.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import personal.shared.exception.BusinessException;
import personal.shared.exception.GenericErrorCode;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =========================================================
    // 409 - BUSINESS EXCEPTION
    // =========================================================

    @ExceptionHandler(BusinessException.class)
    public ProblemDetail handleBusinessException(
            BusinessException ex
    ) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                ex.getMessage()
        );

        problem.setProperty("code", ex.getCode());

        return problem;
    }

    // =========================================================
    // 409 - DATA INTEGRITY
    // =========================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Data integrity violation path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
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
    // 400 - VALIDATION
    // =========================================================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Validation error path={}",
                request.getRequestURI()
        );

        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField() + ": " + error.getDefaultMessage()
                )
                .findFirst()
                .orElse("Los datos enviados no son válidos");

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                detail
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
    // 400 - ARGUMENTO INVÁLIDO
    // =========================================================

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn(
                "Illegal argument path={} message={}",
                request.getRequestURI(),
                ex.getMessage()
        );

        String detail = ex.getMessage() != null
                ? ex.getMessage()
                : "Los datos enviados no son válidos";

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                detail
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INVALID_ARGUMENT.name()
        );

        return problem;
    }

    // =========================================================
    // 500 - ÚLTIMO RECURSO
    // =========================================================

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
                "Ocurrió un error interno en el servidor"
        );

        problem.setProperty(
                "code",
                GenericErrorCode.INTERNAL_ERROR.name()
        );

        return problem;
    }
}