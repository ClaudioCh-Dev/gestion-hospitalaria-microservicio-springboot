package personal.appointment_ms.dto;

public record AppointmentTypeResponse(

        Long id,

        String title,

        String description,

        Boolean active
) {
}