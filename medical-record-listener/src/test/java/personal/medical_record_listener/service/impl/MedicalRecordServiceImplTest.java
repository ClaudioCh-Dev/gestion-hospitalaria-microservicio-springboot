package personal.medical_record_listener.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import personal.medical_record_listener.dto.MedicalRecordResponse;
import personal.medical_record_listener.dto.MedicalRecordSummaryResponse;
import personal.medical_record_listener.exceptions.MedicalRecordErrorCode;
import personal.medical_record_listener.model.MedicalRecord;
import personal.medical_record_listener.repository.MedicalRecordRepository;
import personal.shared.event.MedicalRecordReadyEvent;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class MedicalRecordServiceImplTest {

    @Mock
    private MedicalRecordRepository repository;

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private MedicalRecordServiceImpl service;

    private MedicalRecordReadyEvent evento() {
        return new MedicalRecordReadyEvent(
                5L, "Consulta", 3L, "Ana Pérez", 7L, "Dr. Ruiz", "Cardiología",
                LocalDateTime.of(2026, 10, 5, 10, 30), "Dolor de pecho", "COMPLETED",
                new BigDecimal("80.00"));
    }

    private MedicalRecord registro(String id, String specialty, String amount) {
        return MedicalRecord.builder()
                .id(id)
                .appointmentId(5L)
                .patientId(3L)
                .specialty(specialty)
                .status("COMPLETED")
                .amount(amount != null ? new BigDecimal(amount) : null)
                .build();
    }

    // ---------- save ----------
    @Test
    void save_citaNueva_insertaRegistroConDatosDelEvento() {
        when(repository.findByAppointmentId(5L)).thenReturn(Optional.empty());

        service.save(evento());

        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);
        verify(repository).save(captor.capture());
        MedicalRecord guardado = captor.getValue();
        assertNull(guardado.getId()); // sin id: Mongo hace un insert
        assertEquals(5L, guardado.getAppointmentId());
        assertEquals("Ana Pérez", guardado.getPatientName());
        assertEquals("Cardiología", guardado.getSpecialty());
        assertEquals(new BigDecimal("80.00"), guardado.getAmount());
    }

    @Test
    void save_eventoRepetido_reutilizaElIdParaNoDuplicar() {
        when(repository.findByAppointmentId(5L))
                .thenReturn(Optional.of(registro("abc123", "Cardiología", "80.00")));

        service.save(evento());

        ArgumentCaptor<MedicalRecord> captor = ArgumentCaptor.forClass(MedicalRecord.class);
        verify(repository).save(captor.capture());
        assertEquals("abc123", captor.getValue().getId()); // con id: Mongo actualiza
    }

    // ---------- findByPatientId ----------
    @Test
    void findByPatientId_conRegistros_devuelvePaginaConvertida() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByPatientId(3L, pageable))
                .thenReturn(new PageImpl<>(List.of(registro("abc123", "Cardiología", "80.00")), pageable, 1));

        Page<MedicalRecordResponse> response = service.findByPatientId(3L, pageable);

        assertEquals(1, response.getTotalElements());
        assertEquals("abc123", response.getContent().get(0).id());
    }

    @Test
    void findByPatientId_sinRegistros_lanzaNotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findByPatientId(3L, pageable)).thenReturn(Page.empty(pageable));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.findByPatientId(3L, pageable));

        assertEquals(MedicalRecordErrorCode.MEDICAL_RECORD_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- findAll (MongoTemplate + Query) ----------
    @Test
    void findAll_sinFiltros_consultaSinCriteriosYArmaLaPagina() {
        Pageable pageable = PageRequest.of(0, 2);
        when(mongoTemplate.count(any(Query.class), eq(MedicalRecord.class))).thenReturn(5L);
        when(mongoTemplate.find(any(Query.class), eq(MedicalRecord.class)))
                .thenReturn(List.of(registro("a", "Cardiología", "80.00"), registro("b", "Pediatría", "50.00")));

        Page<MedicalRecordResponse> response = service.findAll(pageable, null, "  ");

        assertEquals(5, response.getTotalElements());
        assertEquals(3, response.getTotalPages());
        assertEquals(2, response.getContent().size());

        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).count(captor.capture(), eq(MedicalRecord.class));
        assertTrue(captor.getValue().getQueryObject().isEmpty()); // filtros vacíos no se aplican
    }

    @Test
    void findAll_conEspecialidadYBusqueda_agregaAmbosCriterios() {
        Pageable pageable = PageRequest.of(0, 10);
        when(mongoTemplate.count(any(Query.class), eq(MedicalRecord.class))).thenReturn(0L);
        when(mongoTemplate.find(any(Query.class), eq(MedicalRecord.class))).thenReturn(List.of());

        service.findAll(pageable, "a.b", "Cardiología");

        ArgumentCaptor<Query> captor = ArgumentCaptor.forClass(Query.class);
        verify(mongoTemplate).count(captor.capture(), eq(MedicalRecord.class));
        var filtro = captor.getValue().getQueryObject();
        assertEquals("Cardiología", filtro.get("specialty"));
        assertTrue(filtro.containsKey("$or"));
        // Pattern.quote: el "." del usuario se busca literal, no como comodín de regex
        assertTrue(filtro.get("$or").toString().contains("\\Qa.b\\E"));
    }

    // ---------- getSummary ----------
    @Test
    void getSummary_calculaTotalesYOrdenaEspecialidades() {
        when(mongoTemplate.count(any(Query.class), eq(MedicalRecord.class))).thenReturn(4L, 3L);
        when(mongoTemplate.findDistinct(any(Query.class), eq("patientId"), eq(MedicalRecord.class), eq(Long.class)))
                .thenReturn(List.of(3L, 8L));
        when(mongoTemplate.find(any(Query.class), eq(MedicalRecord.class))).thenReturn(List.of(
                registro("a", null, "80.00"),
                registro("b", null, "50.50"),
                registro("c", null, null))); // sin monto: se ignora
        List<String> especialidades = new java.util.ArrayList<>();
        especialidades.add("Pediatría");
        especialidades.add(null);
        especialidades.add("Cardiología");
        when(mongoTemplate.findDistinct(any(Query.class), eq("specialty"), eq(MedicalRecord.class), eq(String.class)))
                .thenReturn(especialidades);

        MedicalRecordSummaryResponse summary = service.getSummary();

        assertEquals(4, summary.totalRecords());     // primer count
        assertEquals(3, summary.completedRecords()); // segundo count
        assertEquals(2, summary.uniquePatients());
        assertEquals(new BigDecimal("130.50"), summary.totalAmount());
        assertEquals(List.of("Cardiología", "Pediatría"), summary.specialties());
    }
}
