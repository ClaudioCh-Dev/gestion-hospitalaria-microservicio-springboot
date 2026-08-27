package personal.appointment_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum AppointmentErrorCode implements ErrorCode {

    APPOINTMENT_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    APPOINTMENT_TYPE_ALREADY_INACTIVE(HttpStatus.CONFLICT.value()),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),

    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    PATIENT_INVALID_REQUEST(HttpStatus.BAD_REQUEST.value()),
    PATIENT_CONFLICT(HttpStatus.CONFLICT.value()),
    PATIENT_VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_CONTENT.value()),
    PATIENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value()),

    DOCTOR_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    DOCTOR_INVALID_REQUEST(HttpStatus.BAD_REQUEST.value()),
    DOCTOR_CONFLICT(HttpStatus.CONFLICT.value()),
    DOCTOR_VALIDATION_ERROR(HttpStatus.UNPROCESSABLE_CONTENT.value()),
    DOCTOR_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value()),
    DOCTOR_NOT_AVAILABLE(HttpStatus.CONFLICT.value());

    private final int status;

    AppointmentErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}