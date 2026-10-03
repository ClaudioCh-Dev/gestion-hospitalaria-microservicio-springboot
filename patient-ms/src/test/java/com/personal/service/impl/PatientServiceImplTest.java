package com.personal.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.personal.dto.PatientDetailResponse;
import com.personal.dto.PatientRequest;
import com.personal.dto.PatientResponse;
import com.personal.entities.Patient;
import com.personal.enums.BloodType;
import com.personal.enums.Gender;
import com.personal.exceptions.PatientErrorCode;
import com.personal.mapper.PatientMapper;
import com.personal.mapper.PatientMapperImpl;
import com.personal.repository.IPatientRepository;
import com.personal.streams.PatientPublisher;

import personal.shared.event.PatientCreateEvent;
import personal.shared.event.PatientUpdateEvent;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class PatientServiceImplTest {

    @Mock
    private IPatientRepository patientRepository;

    @Mock
    private PatientPublisher patientPublisher;

    @Spy
    private PatientMapper patientMapper = new PatientMapperImpl();

    @InjectMocks
    private PatientServiceImpl service;

    private PatientRequest request(String email) {
        return new PatientRequest(
                "12345678", "Ana", "Pérez", LocalDate.of(1990, 5, 10),
                Gender.FEMALE, "999888777", email, "Av. Lima 123",
                BloodType.O_POSITIVE, null);
    }

    private Patient paciente() {
        return Patient.builder()
                .id(1L)
                .documentNumber("12345678")
                .firstName("Ana")
                .lastName("Pérez")
                .email("ana@mail.com")
                .build();
    }

    private void saveAsignaId() {
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(1L);
            }
            return entity;
        });
    }

    // ---------- findAll ----------
    @Test
    void findAll_convierteBusquedaEnPatronLike() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientResponse> pagina = new PageImpl<>(List.of(), pageable, 0);
        when(patientRepository.findAllResponses(Gender.FEMALE, "%ana%", pageable)).thenReturn(pagina);

        Page<PatientResponse> response = service.findAll(pageable, Gender.FEMALE, "  Ana ");

        assertEquals(pagina, response);
    }

    @Test
    void findAll_busquedaVacia_pasaNullParaIgnorarElFiltro() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<PatientResponse> pagina = new PageImpl<>(List.of(), pageable, 0);
        when(patientRepository.findAllResponses(isNull(), isNull(), eq(pageable))).thenReturn(pagina);

        assertEquals(pagina, service.findAll(pageable, null, "   "));
    }

    // ---------- findById / findByDocumentNumber ----------
    @Test
    void findById_cuandoExiste_devuelveDetalle() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente()));

        PatientDetailResponse response = service.findById(1L);

        assertEquals("Ana", response.firstName());
        assertEquals("12345678", response.documentNumber());
    }

    @Test
    void findById_cuandoNoExiste_lanzaNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.findById(99L));

        assertEquals(PatientErrorCode.PATIENT_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    @Test
    void findByDocumentNumber_cuandoExiste_devuelveRespuesta() {
        when(patientRepository.findByDocumentNumber("12345678")).thenReturn(Optional.of(paciente()));

        assertEquals(1L, service.findByDocumentNumber("12345678").id());
    }

    @Test
    void findByDocumentNumber_cuandoNoExiste_lanzaNotFound() {
        when(patientRepository.findByDocumentNumber("000")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.findByDocumentNumber("000"));

        assertEquals(PatientErrorCode.PATIENT_NOT_FOUND.toString(), ex.getCode());
    }

    // ---------- create ----------
    @Test
    void create_datosValidos_guardaYPublicaEvento() {
        when(patientRepository.existsByDocumentNumber("12345678")).thenReturn(false);
        when(patientRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        saveAsignaId();

        PatientResponse response = service.create(request("ana@mail.com"));

        assertEquals(1L, response.id());
        assertEquals("Ana", response.firstName());

        ArgumentCaptor<PatientCreateEvent> captor = ArgumentCaptor.forClass(PatientCreateEvent.class);
        verify(patientPublisher).publishPatientCreated(captor.capture());
        assertEquals(1L, captor.getValue().id());
        assertEquals("ana@mail.com", captor.getValue().email());
    }

    @Test
    void create_sinEmail_noValidaEmailDuplicado() {
        when(patientRepository.existsByDocumentNumber("12345678")).thenReturn(false);
        saveAsignaId();

        PatientResponse response = service.create(request(null));

        assertNull(response.email());
        verify(patientRepository, never()).existsByEmail(any());
    }

    @Test
    void create_documentoDuplicado_lanzaConflictYNoGuarda() {
        when(patientRepository.existsByDocumentNumber("12345678")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request("ana@mail.com")));

        assertEquals(PatientErrorCode.PATIENT_DOCUMENT_ALREADY_EXISTS.toString(), ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(patientRepository, never()).save(any());
        verify(patientPublisher, never()).publishPatientCreated(any());
    }

    @Test
    void create_emailDuplicado_lanzaConflictYNoGuarda() {
        when(patientRepository.existsByDocumentNumber("12345678")).thenReturn(false);
        when(patientRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(request("ana@mail.com")));

        assertEquals(PatientErrorCode.PATIENT_EMAIL_ALREADY_EXISTS.toString(), ex.getCode());
        verify(patientRepository, never()).save(any());
        verify(patientPublisher, never()).publishPatientCreated(any());
    }

    // ---------- update ----------
    @Test
    void update_datosValidos_actualizaYPublicaEvento() {
        Patient existente = paciente();
        when(patientRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(patientRepository.existsByDocumentNumberAndIdNot("12345678", 1L)).thenReturn(false);
        when(patientRepository.existsByEmailAndIdNot("nuevo@mail.com", 1L)).thenReturn(false);
        saveAsignaId();

        PatientResponse response = service.update(1L, request("nuevo@mail.com"));

        assertEquals("nuevo@mail.com", response.email());
        verify(patientRepository).save(existente); // actualiza la misma entidad

        ArgumentCaptor<PatientUpdateEvent> captor = ArgumentCaptor.forClass(PatientUpdateEvent.class);
        verify(patientPublisher).publishPatientUpdated(captor.capture());
        assertEquals("nuevo@mail.com", captor.getValue().email());
    }

    @Test
    void update_cuandoNoExiste_lanzaNotFound() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(99L, request("ana@mail.com")));

        assertEquals(PatientErrorCode.PATIENT_NOT_FOUND.toString(), ex.getCode());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void update_documentoDeOtroPaciente_lanzaConflict() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente()));
        when(patientRepository.existsByDocumentNumberAndIdNot("12345678", 1L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, request("ana@mail.com")));

        assertEquals(PatientErrorCode.PATIENT_DOCUMENT_ALREADY_EXISTS.toString(), ex.getCode());
        verify(patientRepository, never()).save(any());
        verify(patientPublisher, never()).publishPatientUpdated(any());
    }

    @Test
    void update_emailDeOtroPaciente_lanzaConflict() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente()));
        when(patientRepository.existsByDocumentNumberAndIdNot("12345678", 1L)).thenReturn(false);
        when(patientRepository.existsByEmailAndIdNot("otro@mail.com", 1L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, request("otro@mail.com")));

        assertEquals(PatientErrorCode.PATIENT_EMAIL_ALREADY_EXISTS.toString(), ex.getCode());
        verify(patientRepository, never()).save(any());
    }

    @Test
    void update_sinEmail_noValidaEmailDuplicado() {
        when(patientRepository.findById(1L)).thenReturn(Optional.of(paciente()));
        when(patientRepository.existsByDocumentNumberAndIdNot("12345678", 1L)).thenReturn(false);
        saveAsignaId();

        service.update(1L, request(null));

        verify(patientRepository, never()).existsByEmailAndIdNot(anyString(), anyLong());
    }

    // ---------- delete ----------
    @Test
    void delete_cuandoExiste_loElimina() {
        when(patientRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(patientRepository).deleteById(1L);
    }

    @Test
    void delete_cuandoNoExiste_lanzaNotFoundYNoElimina() {
        when(patientRepository.existsById(99L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(99L));

        assertEquals(PatientErrorCode.PATIENT_NOT_FOUND.toString(), ex.getCode());
        verify(patientRepository, never()).deleteById(any());
    }
}
