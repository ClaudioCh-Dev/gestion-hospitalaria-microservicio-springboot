
package personal.appointment_ms.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import personal.appointment_ms.entities.Appointment;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);
}