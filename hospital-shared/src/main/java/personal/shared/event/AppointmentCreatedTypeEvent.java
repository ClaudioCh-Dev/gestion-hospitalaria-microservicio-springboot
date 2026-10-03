package personal.shared.event;

import java.math.BigDecimal;

public record AppointmentCreatedTypeEvent (
     Long id,
     String title,
     String description,
     Boolean active,
     // Precio inicial del tipo de cita: billing-ms crea la tarifa con este valor
     BigDecimal price
) {
}
