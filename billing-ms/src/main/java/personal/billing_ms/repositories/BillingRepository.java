package personal.billing_ms.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;

public interface BillingRepository
        extends JpaRepository<BillingRecord, Long>,
        JpaSpecificationExecutor<BillingRecord> {

    Page<BillingRecord> findByPatientId(
            Long patientId,
            Pageable pageable
    );

    // Cantidad y monto por estado, para el resumen
    @Query("""
            SELECT b.status AS status,
                   COUNT(b) AS count,
                   COALESCE(SUM(b.amount), 0) AS amount
            FROM BillingRecord b
            GROUP BY b.status
            """)
    List<StatusTotal> totalsByStatus();

    @Query("""
            SELECT COALESCE(SUM(b.amount), 0)
            FROM BillingRecord b
            WHERE b.status = :status
              AND b.paidAt >= :from
            """)
    BigDecimal sumByStatusSince(
            @Param("status") BillingStatus status,
            @Param("from") LocalDateTime from
    );

    interface StatusTotal {
        BillingStatus getStatus();

        Long getCount();

        BigDecimal getAmount();
    }
}
