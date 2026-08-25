package personal.medical_record_listener.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum MedicalRecordErrorCode implements ErrorCode {

    MEDICAL_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND.value());

    private final int status;

    MedicalRecordErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}