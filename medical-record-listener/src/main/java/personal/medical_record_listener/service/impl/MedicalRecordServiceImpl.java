package personal.medical_record_listener.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.exceptions.MedicalRecordErrorCode;
import personal.medical_record_listener.model.MedicalRecord;
import personal.medical_record_listener.repository.MedicalRecordRepository;
import personal.medical_record_listener.service.IMedicalRecordService;

import personal.shared.event.MedicalRecordReadyEvent;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements IMedicalRecordService {

    private final MedicalRecordRepository repository;

    // Guardar registro médico cuando la cita está COMPLETED
    // y el pago está PAID
    @Override
    public void save(MedicalRecordReadyEvent event) {

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

    // Obtener registros médicos de un paciente
    @Override
    public Page<MedicalRecordResponse> findByPatientId(
            Long patientId,
            Pageable pageable) {

        Page<MedicalRecord> records =
                repository.findByPatientId(patientId, pageable);

        if (records.isEmpty()) {
            throw new BusinessException(
                    MedicalRecordErrorCode.MEDICAL_RECORD_NOT_FOUND,
                    "Historial médico no encontrado para el paciente"
            );
        }

        return records.map(this::toResponse);
    }

    // Obtener todos los registros médicos
    @Override
    public Page<MedicalRecordResponse> findAll(Pageable pageable) {

        return repository.findAll(pageable)
                .map(this::toResponse);
    }

    private MedicalRecordResponse toResponse(
            MedicalRecord record) {

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