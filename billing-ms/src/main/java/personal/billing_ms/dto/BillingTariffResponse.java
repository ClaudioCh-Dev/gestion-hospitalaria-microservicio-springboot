package personal.billing_ms.dto;

import java.math.BigDecimal;

public record BillingTariffResponse(
        Long appointmentTypeId,
        BigDecimal price,
        String currency
) {
}