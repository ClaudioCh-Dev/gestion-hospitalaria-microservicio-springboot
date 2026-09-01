package personal.shared.exception;

public class BusinessException extends RuntimeException {

    private final String code;
    private final int status;

    // Para errores propios del microservicio
    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code.toString();
        this.status = code.status();
    }

    // Para errores recibidos desde otro microservicio
    public BusinessException(String code, int status, String message) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }
}