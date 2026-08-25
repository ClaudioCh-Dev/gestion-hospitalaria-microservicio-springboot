package personal.notification_ms.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum NotificationErrorCode implements ErrorCode {

    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND.value());

    private final int status;

    NotificationErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}