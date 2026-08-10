package personal.appointment_ms.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateAppointmentTypeRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price
) {
}