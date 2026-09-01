package personal.appointment_ms.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "doctors_appointment")
public class DoctorEntity {

    @Id
    private Long id;

    private String fullName;

    private String specialty;
}