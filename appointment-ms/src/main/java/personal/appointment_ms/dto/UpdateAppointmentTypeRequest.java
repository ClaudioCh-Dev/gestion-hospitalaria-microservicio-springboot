package personal.appointment_ms.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import personal.appointment_ms.entities.AppointmentTypeColor;

public record UpdateAppointmentTypeRequest(

        @NotBlank
        String title,

        String description,

        Boolean active,

        // Opcional: si no se envía se conserva el color actual
        @Pattern(regexp = AppointmentTypeColor.PATTERN, message = AppointmentTypeColor.MESSAGE)
        String color
) {
}