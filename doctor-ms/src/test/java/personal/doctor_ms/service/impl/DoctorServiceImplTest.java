package personal.doctor_ms.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import personal.doctor_ms.client.UserClient;
import personal.doctor_ms.client.dto.CreateDoctorRequestClient;
import personal.doctor_ms.client.dto.UserResponse;
import personal.doctor_ms.dtos.CreateDoctorRequest;
import personal.doctor_ms.dtos.CreateSpecialtyRequest;
import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.dtos.SpecialtyResponse;
import personal.doctor_ms.dtos.UpdateDoctorRequest;
import personal.doctor_ms.entities.Doctor;
import personal.doctor_ms.entities.Specialty;
import personal.doctor_ms.exceptions.DoctorErrorCode;
import personal.doctor_ms.mapper.DoctorMapper;
import personal.doctor_ms.mapper.DoctorMapperImpl;
import personal.doctor_ms.mapper.SpecialtyMapper;
import personal.doctor_ms.mapper.SpecialtyMapperImpl;
import personal.doctor_ms.repositories.DoctorRepository;
import personal.doctor_ms.repositories.SpecialtyRepository;
import personal.doctor_ms.security.UserContext;
import personal.doctor_ms.security.UserContextHolder;
import personal.doctor_ms.stream.DoctorPublisher;
import personal.shared.event.DoctorCreatedEvent;
import personal.shared.event.DoctorUpdateEvent;
import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class DoctorServiceImplTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private SpecialtyRepository specialtyRepository;

    @Spy
    private DoctorMapper doctorMapper = new DoctorMapperImpl();

    @Spy
    private SpecialtyMapper specialtyMapper = new SpecialtyMapperImpl();

    @Mock
    private UserClient userClient;

    @Mock
    private DoctorPublisher doctorPublisher;

    @InjectMocks
    private DoctorServiceImpl service;

    @AfterEach
    void limpiarUsuario() {
        UserContextHolder.clear();
    }

    private Specialty cardiologia() {
        return Specialty.builder().id(2L).name("Cardiología").description("Corazón").build();
    }

    private Doctor doctor() {
        return Doctor.builder()
                .id(1L)
                .userId(20L)
                .licenseNumber("CMP-123")
                .firstName("Luis")
                .lastName("Ruiz")
                .email("luis@hospital.com")
                .specialty(cardiologia())
                .build();
    }

    private CreateDoctorRequest createRequest() {
        return new CreateDoctorRequest("CMP-123", "Luis", "Ruiz", "luis@hospital.com",
                "999888777", 2L, LocalTime.of(8, 0), LocalTime.of(14, 0));
    }

    private UpdateDoctorRequest updateRequest(String email, Boolean active) {
        return new UpdateDoctorRequest("Luis A.", "Ruiz", email, "911222333", 2L,
                LocalTime.of(9, 0), LocalTime.of(15, 0), active);
    }

    private void saveAsignaId() {
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> {
            Doctor entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(1L);
            }
            return entity;
        });
    }

    // ---------- findAll / findBySpecialty ----------
    @Test
    void findAll_sinEspecialidad_buscaConPatronLike() {
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorRepository.search(null, "%luis%", pageable))
                .thenReturn(new PageImpl<>(List.of(doctor()), pageable, 1));

        Page<DoctorResponse> response = service.findAll(pageable, " Luis ");

        assertEquals(1, response.getTotalElements());
        assertEquals("Cardiología", response.getContent().get(0).specialtyName());
    }

    @Test
    void findBySpecialty_busquedaVacia_filtraSoloPorEspecialidad() {
        Pageable pageable = PageRequest.of(0, 10);
        when(doctorRepository.search(eq(2L), isNull(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(doctor()), pageable, 1));

        Page<DoctorResponse> response = service.findBySpecialty(2L, pageable, "");

        assertEquals(2L, response.getContent().get(0).specialtyId());
    }

    // ---------- findById ----------
    @Test
    void findById_cuandoExiste_devuelveRespuesta() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor()));

        assertEquals("CMP-123", service.findById(1L).licenseNumber());
    }

    @Test
    void findById_cuandoNoExiste_lanzaNotFound() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.findById(99L));

        assertEquals(DoctorErrorCode.DOCTOR_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- findMe ----------
    @Test
    void findMe_usuarioConMedico_devuelveSuPerfil() {
        UserContextHolder.set(new UserContext(20L, "DOCTOR", Set.of()));
        when(doctorRepository.findByUserId(20L)).thenReturn(Optional.of(doctor()));

        assertEquals(1L, service.findMe().id());
    }

    @Test
    void findMe_sinUsuarioLogueado_lanzaNotFoundSinConsultar() {
        BusinessException ex = assertThrows(BusinessException.class, () -> service.findMe());

        assertEquals(DoctorErrorCode.DOCTOR_NOT_FOUND.toString(), ex.getCode());
        verify(doctorRepository, never()).findByUserId(anyLong());
    }

    @Test
    void findMe_usuarioSinMedico_lanzaNotFound() {
        UserContextHolder.set(new UserContext(30L, "ADMIN", Set.of()));
        when(doctorRepository.findByUserId(30L)).thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> service.findMe());
    }

    // ---------- create ----------
    @Test
    void create_datosValidos_creaUsuarioGuardaYPublica() {
        when(doctorRepository.existsByEmail("luis@hospital.com")).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber("CMP-123")).thenReturn(false);
        when(specialtyRepository.findById(2L)).thenReturn(Optional.of(cardiologia()));
        when(userClient.createDoctor(any(CreateDoctorRequestClient.class)))
                .thenReturn(new UserResponse(20L, "luis@hospital.com", 2L, "DOCTOR", true));
        saveAsignaId();

        DoctorResponse response = service.create(createRequest());

        assertEquals(1L, response.id());
        assertEquals(20L, response.userId()); // el id del usuario que devolvió auth-server
        verify(userClient).createDoctor(new CreateDoctorRequestClient("luis@hospital.com"));

        ArgumentCaptor<DoctorCreatedEvent> captor = ArgumentCaptor.forClass(DoctorCreatedEvent.class);
        verify(doctorPublisher).publishDoctorCreated(captor.capture());
        assertEquals(1L, captor.getValue().doctorId());
        assertEquals("Cardiología", captor.getValue().specialty());
    }

    @Test
    void create_emailDuplicado_lanzaConflictSinCrearUsuario() {
        when(doctorRepository.existsByEmail("luis@hospital.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(createRequest()));

        assertEquals(DoctorErrorCode.DOCTOR_ALREADY_EXISTS.toString(), ex.getCode());
        verify(userClient, never()).createDoctor(any());
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void create_licenciaDuplicada_lanzaConflictSinCrearUsuario() {
        when(doctorRepository.existsByEmail("luis@hospital.com")).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber("CMP-123")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(createRequest()));

        assertEquals(DoctorErrorCode.DOCTOR_LICENSE_NUMBER_ALREADY_EXISTS.toString(), ex.getCode());
        assertEquals(409, ex.getStatus());
        verify(userClient, never()).createDoctor(any()); // no queda un usuario huérfano en auth-server
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void create_especialidadNoExiste_lanzaNotFoundSinCrearUsuario() {
        when(doctorRepository.existsByEmail("luis@hospital.com")).thenReturn(false);
        when(doctorRepository.existsByLicenseNumber("CMP-123")).thenReturn(false);
        when(specialtyRepository.findById(2L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(createRequest()));

        assertEquals(DoctorErrorCode.SPECIALTY_NOT_FOUND.toString(), ex.getCode());
        verify(userClient, never()).createDoctor(any());
    }

    // ---------- specialties ----------
    @Test
    void findAllSpecialties_devuelveTodasConvertidas() {
        when(specialtyRepository.findAll()).thenReturn(List.of(cardiologia()));

        List<SpecialtyResponse> response = service.findAllSpecialties();

        assertEquals(1, response.size());
        assertEquals("Cardiología", response.get(0).name());
    }

    @Test
    void createSpecialty_guardaYDevuelveRespuesta() {
        when(specialtyRepository.save(any(Specialty.class))).thenAnswer(invocation -> {
            Specialty entity = invocation.getArgument(0);
            entity.setId(5L);
            return entity;
        });

        SpecialtyResponse response = service.createSpecialty(
                new CreateSpecialtyRequest("Pediatría", "Niños"));

        assertEquals(5L, response.id());
        assertEquals("Pediatría", response.name());
    }

    // ---------- update ----------
    @Test
    void update_datosValidos_guardaAntesDePublicar() {
        Doctor existente = doctor();
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(doctorRepository.existsByEmailAndIdNot("nuevo@hospital.com", 1L)).thenReturn(false);
        when(specialtyRepository.findById(2L)).thenReturn(Optional.of(cardiologia()));
        saveAsignaId();

        DoctorResponse response = service.update(1L, updateRequest("nuevo@hospital.com", false));

        assertEquals("Luis A.", response.firstName());
        assertEquals("nuevo@hospital.com", response.email());
        assertFalse(response.active());

        // InOrder: exige que save ocurra ANTES que publish
        InOrder orden = inOrder(doctorRepository, doctorPublisher);
        orden.verify(doctorRepository).save(existente);
        orden.verify(doctorPublisher).publishDoctorUpdated(any(DoctorUpdateEvent.class));
    }

    @Test
    void update_sinActive_conservaElEstado() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor()));
        when(doctorRepository.existsByEmailAndIdNot("luis@hospital.com", 1L)).thenReturn(false);
        when(specialtyRepository.findById(2L)).thenReturn(Optional.of(cardiologia()));
        saveAsignaId();

        DoctorResponse response = service.update(1L, updateRequest("luis@hospital.com", null));

        assertTrue(response.active());
    }

    @Test
    void update_cuandoNoExiste_lanzaNotFoundYNoPublica() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(99L, updateRequest("luis@hospital.com", true)));

        assertEquals(DoctorErrorCode.DOCTOR_NOT_FOUND.toString(), ex.getCode());
        verify(doctorRepository, never()).save(any());
        verify(doctorPublisher, never()).publishDoctorUpdated(any());
    }

    @Test
    void update_emailDeOtroMedico_lanzaConflictYNoPublica() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor()));
        when(doctorRepository.existsByEmailAndIdNot("otro@hospital.com", 1L)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, updateRequest("otro@hospital.com", true)));

        assertEquals(DoctorErrorCode.DOCTOR_ALREADY_EXISTS.toString(), ex.getCode());
        verify(doctorRepository, never()).save(any());
        verify(doctorPublisher, never()).publishDoctorUpdated(any());
    }

    @Test
    void update_especialidadNoExiste_lanzaNotFoundYNoPublica() {
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor()));
        when(doctorRepository.existsByEmailAndIdNot("luis@hospital.com", 1L)).thenReturn(false);
        when(specialtyRepository.findById(2L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(1L, updateRequest("luis@hospital.com", true)));

        assertEquals(DoctorErrorCode.SPECIALTY_NOT_FOUND.toString(), ex.getCode());
        verify(doctorPublisher, never()).publishDoctorUpdated(any());
    }
}
