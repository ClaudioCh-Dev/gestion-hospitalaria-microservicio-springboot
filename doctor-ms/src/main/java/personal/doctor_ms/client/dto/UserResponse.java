package personal.doctor_ms.client;

public record UserResponse(
        Long id,
        String username,
        Long roleId,
        String role,
        boolean active
) {}