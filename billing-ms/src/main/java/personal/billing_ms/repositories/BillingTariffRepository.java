package personal.billing_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.billing_ms.entities.BillingTariff;

public interface BillingTariffRepository extends JpaRepository<BillingTariff, Long> {
    
}
