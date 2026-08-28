package personal.appointment_ms.dto;

import jakarta.validation.constraints.NotBlank;;

public record UpdateAppointmentTypeRequest(

        @NotBlank
        String title,

        String description,

        Boolean active
) {
}