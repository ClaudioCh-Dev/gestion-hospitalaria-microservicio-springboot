package personal.appointment_ms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "patients_appointment")
public class PatientEntity {

    @Id
    private Long id;

    private String fullName;
}