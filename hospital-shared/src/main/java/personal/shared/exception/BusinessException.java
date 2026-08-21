package personal.shared.exception;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(Enum<?> code, String message) {
        super(message);
        this.code = code.name();
    }

    public String getCode() {
        return code;
    }
}