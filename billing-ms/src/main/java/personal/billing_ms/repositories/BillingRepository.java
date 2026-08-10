package personal.billing_ms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.billing_ms.entities.BillingRecord;

public interface BillingRepository extends JpaRepository<BillingRecord, Long> {
    
    List<BillingRecord> findByPatientId(Long patientId);
}
