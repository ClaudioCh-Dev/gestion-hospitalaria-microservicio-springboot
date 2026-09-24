package personal.billing_ms.dto;

import java.math.BigDecimal;

// Totales de facturación calculados en base de datos (montos por estado y cobrado en el mes actual)
public record BillingSummaryResponse(
        long totalCount,
        BigDecimal totalAmount,
        long pendingCount,
        BigDecimal pendingAmount,
        long paidCount,
        BigDecimal paidAmount,
        long cancelledCount,
        BigDecimal cancelledAmount,
        BigDecimal paidThisMonthAmount
) {
}
