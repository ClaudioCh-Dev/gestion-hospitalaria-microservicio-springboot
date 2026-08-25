package personal.gateway.exceptions;

import org.springframework.http.HttpStatus;

import personal.shared.exception.ErrorCode;

public enum GatewayErrorCode implements ErrorCode {

    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE.value());

    private final int status;

    GatewayErrorCode(int status) {
        this.status = status;
    }

    @Override
    public int status() {
        return status;
    }
}