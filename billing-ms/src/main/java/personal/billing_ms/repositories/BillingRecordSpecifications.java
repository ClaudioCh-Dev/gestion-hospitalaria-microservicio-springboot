package personal.billing_ms.repositories;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import personal.billing_ms.entities.BillingRecord;
import personal.billing_ms.entities.BillingStatus;

// Filtros opcionales del listado de facturas; los que llegan null no se aplican
public final class BillingRecordSpecifications {

    private BillingRecordSpecifications() {
    }

    public static Specification<BillingRecord> filter(
            BillingStatus status,
            String search,
            List<Long> patientIds) {

        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(cb.equal(root.get("status"), status));
            }

            if (patientIds != null && !patientIds.isEmpty()) {
                predicates.add(root.get("patientId").in(patientIds));
            }

            if (search != null && !search.isBlank()) {
                String term = search.trim().replace("#", "");

                // billing-ms no guarda nombres: el texto solo puede ser un número de factura, cita o paciente
                if (term.matches("\\d{1,18}")) {
                    Long number = Long.valueOf(term);

                    predicates.add(cb.or(
                            cb.equal(root.get("id"), number),
                            cb.equal(root.get("appointmentId"), number),
                            cb.equal(root.get("patientId"), number)));
                } else {
                    predicates.add(cb.disjunction());
                }
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }
}
