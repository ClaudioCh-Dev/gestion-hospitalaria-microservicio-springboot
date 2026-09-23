package personal.billing_ms.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import personal.billing_ms.entities.BillingRecord;

public interface BillingRepository
        extends JpaRepository<BillingRecord, Long> {

    Page<BillingRecord> findByPatientId(
            Long patientId,
            Pageable pageable
    );
}