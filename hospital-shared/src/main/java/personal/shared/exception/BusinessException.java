package personal.shared.exception;

public class BusinessException extends RuntimeException {

    private final String code;
    private final int status;

    public BusinessException(ErrorCode code, String message) {

        super(message);

        this.code = code.toString();
        this.status = code.status();
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}