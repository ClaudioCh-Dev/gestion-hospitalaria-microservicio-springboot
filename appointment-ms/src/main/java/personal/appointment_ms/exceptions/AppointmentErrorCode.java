package personal.appointment_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum AppointmentErrorCode implements ErrorCode {

    APPOINTMENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    APPOINTMENT_TYPE_ALREADY_INACTIVE(HttpStatus.CONFLICT.value()),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),

    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND.value()),

    PATIENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value()),
    DOCTOR_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value());

    private final int status;

    AppointmentErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}