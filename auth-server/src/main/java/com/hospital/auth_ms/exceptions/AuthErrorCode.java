package com.hospital.auth_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    AUTH_INVALID_CREDENTIALS(
            HttpStatus.UNAUTHORIZED.value()),

    AUTH_INVALID_TOKEN(
            HttpStatus.UNAUTHORIZED.value()),

    AUTH_INVALID_REFRESH_TOKEN(
            HttpStatus.UNAUTHORIZED.value());

    private final int status;

    AuthErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}