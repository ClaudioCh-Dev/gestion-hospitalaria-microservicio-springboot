package personal.appointment_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.appointment_ms.entities.AppointmentType;

public interface AppointmentTypeRepository extends JpaRepository<AppointmentType, Long> {
    
}
