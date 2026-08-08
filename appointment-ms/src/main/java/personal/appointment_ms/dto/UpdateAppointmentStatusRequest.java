package personal.appointment_ms.dto;

import jakarta.validation.constraints.NotNull;
import personal.appointment_ms.entities.AppointmentStatus;

public record UpdateAppointmentStatusRequest(

        @NotNull
        AppointmentStatus status

) {
}