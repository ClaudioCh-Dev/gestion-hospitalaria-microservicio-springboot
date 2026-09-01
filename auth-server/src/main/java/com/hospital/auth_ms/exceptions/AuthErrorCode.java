package com.hospital.auth_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    AUTH_INVALID_CREDENTIALS(
            HttpStatus.UNAUTHORIZED.value()),

    AUTH_INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED.value()),

    AUTH_INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED.value()),

    AUTH_USER_INACTIVE(
            HttpStatus.UNAUTHORIZED.value()),

    INVALID_ACTIVATION_TOKEN(
            HttpStatus.BAD_REQUEST.value()),

    ACTIVATION_TOKEN_EXPIRED(
            HttpStatus.BAD_REQUEST.value()),

    USER_ADMIN_CANNOT_BE_DEACTIVATED(
            HttpStatus.FORBIDDEN.value()),
            
    USER_ALREADY_ACTIVE(
            HttpStatus.BAD_REQUEST.value()),
            
    USER_NOT_FOUND(
            HttpStatus.NOT_FOUND.value()),

    INVALID_PASSWORD(
            HttpStatus.BAD_REQUEST.value()),

    EMAIL_ALREADY_EXISTS(
            HttpStatus.BAD_REQUEST.value()),
            
    EMAIL_SEND_FAILED(
            HttpStatus.SERVICE_UNAVAILABLE.value());

            
    private final int status;

    AuthErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}