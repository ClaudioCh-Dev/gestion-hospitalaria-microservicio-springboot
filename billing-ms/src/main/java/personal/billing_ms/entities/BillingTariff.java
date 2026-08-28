package personal.billing_ms.entities;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "billing_appointment_type_id", nullable = false, unique = true)
    private Long billingAppointmentTypeId;

    @Column(name = "billing_appointment_type_name", nullable = false)
    private String billingAppointmentTypeName;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "PEN";

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = false;
}