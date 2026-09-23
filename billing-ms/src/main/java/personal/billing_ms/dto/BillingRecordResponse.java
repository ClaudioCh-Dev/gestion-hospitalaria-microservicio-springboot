package personal.billing_ms.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import personal.billing_ms.entities.BillingStatus;

public record BillingRecordResponse(
        Long id,
        Long appointmentId,
        Long patientId,
        BigDecimal amount,
        String currency,
        BillingStatus status,
        LocalDateTime issuedAt,
        LocalDateTime paidAt
) {}