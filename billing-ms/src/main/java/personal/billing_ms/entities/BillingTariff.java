package personal.billing_ms.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "billing_tariffs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingTariff {

    @Id
    @Column(name = "billing_appointment_type_id", nullable = false)
    private Long billingAppointmentTypeId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "PEN";
}