package personal.appointment_ms.dto;

import java.math.BigDecimal;

public record AppointmentTypeResponse(

        Long id,

        String title,

        String description,

        BigDecimal price,

        Boolean active
) {
}