package personal.appointment_ms.client.dto;
import java.math.BigDecimal;

public record BillingTariffResponse(
        Long billingAppointmentTypeId,
        BigDecimal price,
        String currency
) {
}