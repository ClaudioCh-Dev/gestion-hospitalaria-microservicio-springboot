package personal.appointment_ms.client.dto;
import java.math.BigDecimal;

public record BillingTariffResponse(
        BigDecimal price,
        String currency,
        Boolean active
) {
}