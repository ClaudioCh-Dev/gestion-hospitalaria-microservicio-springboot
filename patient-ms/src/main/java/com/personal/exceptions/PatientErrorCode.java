package com.personal.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum PatientErrorCode implements ErrorCode {

    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    PATIENT_DOCUMENT_ALREADY_EXISTS(HttpStatus.CONFLICT.value()),
    PATIENT_EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT.value());

    private final int status;

    PatientErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}