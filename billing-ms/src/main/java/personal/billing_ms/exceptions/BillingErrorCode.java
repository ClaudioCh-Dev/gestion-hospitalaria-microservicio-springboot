package personal.billing_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum BillingErrorCode implements ErrorCode {
    BILLING_RECORD_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    APPOINTMENT_NOT_FOUND(HttpStatus.NOT_FOUND.value()),
    BILLING_RECORD_ALREADY_PAID(HttpStatus.BAD_REQUEST.value()),
    BILLING_RECORD_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST.value());

    private final int status;

    BillingErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}
