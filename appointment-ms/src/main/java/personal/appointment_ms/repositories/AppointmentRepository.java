
package personal.appointment_ms.repositories;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import feign.Param;
import personal.appointment_ms.entities.Appointment;

public interface AppointmentRepository
        extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1
                FROM appointments a
                WHERE a.doctor_id = :doctorId
                  AND a.status <> 'CANCELLED'
                  AND a.scheduled_at < :newEnd
                  AND (:newStart < a.scheduled_at +
                       (a.duration_minutes * INTERVAL '1 minute'))
            )
            """, nativeQuery = true)
    boolean existsOverlappingAppointment(
            @Param("doctorId") Long doctorId,
            @Param("newStart") LocalDateTime newStart,
            @Param("newEnd") LocalDateTime newEnd);
}