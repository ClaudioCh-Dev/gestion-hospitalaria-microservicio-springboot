package personal.shared.exception;

public record RemoteProblemDetail(
        Integer status,
        String detail,
        String code
) {
}