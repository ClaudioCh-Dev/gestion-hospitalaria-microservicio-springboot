package personal.billing_ms.dto;

import java.math.BigDecimal;

public record CreateBillingTariffRequest(
        Long appointmentTypeId,
        BigDecimal price,
        String currency
) {
}
