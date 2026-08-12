package personal.medical_record_listener.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.exceptions.MedicalRecordNotFoundException;
import personal.medical_record_listener.model.MedicalRecord;
import personal.medical_record_listener.repository.MedicalRecordRepository;
import personal.shared.event.AppointmentEvent;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalRecordService {

    private final MedicalRecordRepository repository;

    // Para guardar un registro médico cuando se crea una cita por el evento
    public void save(AppointmentEvent event) {

        MedicalRecord record = MedicalRecord.builder()
                .appointmentId(event.appointmentId())
                .patientId(event.patientId())
                .patientName(event.patientName())
                .doctorId(event.doctorId())
                .doctorName(event.doctorName())
                .specialty(event.specialty())
                .scheduledAt(event.scheduledAt())
                .reason(event.reason())
                .status(event.status())
                .amount(event.amount())
                .build();

        repository.save(record);
    }

    // Para obtener todos los registros médicos de un paciente
    public List<MedicalRecordResponse> findByPatientId(Long patientId) {

        List<MedicalRecord> records = repository.findByPatientId(patientId);

        if (records.isEmpty()) {
            throw new MedicalRecordNotFoundException(patientId);
        }

        return records.stream()
                .map(this::toResponse)
                .toList();
    }

    // Para obtener todos los registros médicos
    public List<MedicalRecordResponse> findAll() {

        return repository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // Para convertir un registro médico a una respuesta
    private MedicalRecordResponse toResponse(MedicalRecord record) {

        return new MedicalRecordResponse(
                record.getId(),
                record.getAppointmentId(),
                record.getPatientId(),
                record.getPatientName(),
                record.getDoctorId(),
                record.getDoctorName(),
                record.getSpecialty(),
                record.getScheduledAt(),
                record.getReason(),
                record.getStatus(),
                record.getAmount()
        );
    }
}