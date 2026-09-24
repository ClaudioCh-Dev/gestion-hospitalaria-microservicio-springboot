package personal.medical_record_listener.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.dto.MedicalRecordSummaryResponse;
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

    private final MongoTemplate mongoTemplate;

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

    // Obtener registros médicos, con búsqueda y filtro por especialidad opcionales
    @Override
    public Page<MedicalRecordResponse> findAll(
            Pageable pageable,
            String search,
            String specialty) {

        Query query = new Query();

        if (specialty != null && !specialty.isBlank()) {
            query.addCriteria(Criteria.where("specialty").is(specialty));
        }

        if (search != null && !search.isBlank()) {
            // Pattern.quote evita que caracteres del usuario se interpreten como regex
            Pattern pattern = Pattern.compile(
                    Pattern.quote(search.trim()),
                    Pattern.CASE_INSENSITIVE);

            query.addCriteria(new Criteria().orOperator(
                    Criteria.where("patientName").regex(pattern),
                    Criteria.where("doctorName").regex(pattern),
                    Criteria.where("specialty").regex(pattern),
                    Criteria.where("reason").regex(pattern)));
        }

        long total = mongoTemplate.count(query, MedicalRecord.class);

        List<MedicalRecordResponse> content = mongoTemplate
                .find(query.with(pageable), MedicalRecord.class)
                .stream()
                .map(this::toResponse)
                .toList();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public MedicalRecordSummaryResponse getSummary() {

        long totalRecords = mongoTemplate.count(new Query(), MedicalRecord.class);

        long uniquePatients = mongoTemplate
                .findDistinct(new Query(), "patientId", MedicalRecord.class, Long.class)
                .size();

        long completedRecords = mongoTemplate.count(
                new Query(Criteria.where("status").is("COMPLETED")),
                MedicalRecord.class);

        // Se suma en Java: amount puede estar guardado como texto según la conversión de BigDecimal
        Query amounts = new Query();
        amounts.fields().include("amount");

        BigDecimal totalAmount = mongoTemplate.find(amounts, MedicalRecord.class)
                .stream()
                .map(MedicalRecord::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        List<String> specialties = mongoTemplate
                .findDistinct(new Query(), "specialty", MedicalRecord.class, String.class)
                .stream()
                .filter(Objects::nonNull)
                .sorted()
                .toList();

        return new MedicalRecordSummaryResponse(
                totalRecords,
                uniquePatients,
                completedRecords,
                totalAmount,
                specialties);
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