package personal.doctor_ms.dtos;

import jakarta.validation.constraints.NotNull;

public record CreateSpecialtyRequest(
        @NotNull
        String name,
        @NotNull
        String description
) {
}