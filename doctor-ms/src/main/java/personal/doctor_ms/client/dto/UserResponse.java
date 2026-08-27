package personal.doctor_ms.client.dto;

public record UserResponse(
        Long id,
        String email,
        Long roleId,
        String role,
        boolean active
) {}