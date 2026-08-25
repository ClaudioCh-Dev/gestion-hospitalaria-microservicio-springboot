package personal.doctor_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum DoctorErrorCode implements ErrorCode {

    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    DOCTOR_LICENSE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT.value()),
    SPECIALTY_NOT_FOUND(HttpStatus.NOT_FOUND.value());

    private final int status;

    DoctorErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}