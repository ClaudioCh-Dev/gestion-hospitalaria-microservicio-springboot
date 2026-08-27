package personal.doctor_ms.client;

public record CreateUserRequestClient(
        String username,
        String password,
        Long roleId
) {}