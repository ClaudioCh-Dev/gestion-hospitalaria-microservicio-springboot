package personal.appointment_ms.dto;
import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import personal.appointment_ms.entities.AppointmentTypeColor;

public record CreateAppointmentTypeRequest(

        @NotBlank
        String title,

        String description,

        @NotNull
        @DecimalMin(value = "0.0", inclusive = false)
        BigDecimal price,

        // Opcional: si no se envía se usa AppointmentTypeColor.DEFAULT
        @Pattern(regexp = AppointmentTypeColor.PATTERN, message = AppointmentTypeColor.MESSAGE)
        String color
) {
}