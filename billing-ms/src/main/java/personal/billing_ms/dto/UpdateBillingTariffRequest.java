package personal.billing_ms.dto;

import java.math.BigDecimal;

public record UpdateBillingTariffRequest(
    BigDecimal price,
    String currency
) {
}
