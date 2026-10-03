package com.hospital.auth_ms.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.auth_ms.dtos.users.ActivateUserRequest;
import com.hospital.auth_ms.dtos.users.ChangePasswordRequest;
import com.hospital.auth_ms.dtos.users.CreateDoctorRequest;
import com.hospital.auth_ms.dtos.users.CreateUserRequest;
import com.hospital.auth_ms.dtos.users.RoleResponse;
import com.hospital.auth_ms.dtos.users.UpdateUserRequest;
import com.hospital.auth_ms.dtos.users.UserResponse;
import com.hospital.auth_ms.entities.RoleEntity;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.repositories.RoleRepository;
import com.hospital.auth_ms.repositories.UserRepository;
import com.hospital.auth_ms.security.UserContext;
import com.hospital.auth_ms.security.UserContextHolder;
import com.hospital.auth_ms.services.IEmailService;
import com.hospital.auth_ms.services.IRefreshTokenService;

import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private RoleRepository roleRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private IRefreshTokenService refreshTokenService;
    @Mock private IEmailService emailService;

    @InjectMocks
    private UserServiceImpl service;

    @AfterEach
    void limpiarUsuario() {
        UserContextHolder.clear();
    }

    private RoleEntity rol(Long id, String name) {
        return RoleEntity.builder().id(id).name(name).build();
    }

    private UserEntity usuario(String rol, boolean active) {
        return UserEntity.builder()
                .id(20L)
                .email("luis@hospital.com")
                .password("$2a$hash")
                .role(rol(2L, rol))
                .active(active)
                .build();
    }

    private void saveAsignaId() {
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity entity = invocation.getArgument(0);
            if (entity.getId() == null) {
                entity.setId(20L);
            }
            return entity;
        });
    }

    // ---------- consultas ----------
    @Test
    void findAll_devuelveUsuariosConvertidos() {
        when(userRepository.findAll()).thenReturn(List.of(usuario("DOCTOR", true)));

        List<UserResponse> response = service.findAll();

        assertEquals(1, response.size());
        assertEquals("DOCTOR", response.get(0).role());
    }

    @Test
    void findAllRoles_devuelveIdYNombre() {
        when(roleRepository.findAll()).thenReturn(List.of(rol(1L, "ADMIN"), rol(2L, "DOCTOR")));

        List<RoleResponse> response = service.findAllRoles();

        assertEquals(new RoleResponse(2L, "DOCTOR"), response.get(1));
    }

    @Test
    void findById_noExiste_lanzaNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> service.findById(99L));

        assertEquals(AuthErrorCode.USER_NOT_FOUND.toString(), ex.getCode());
        assertEquals(404, ex.getStatus());
    }

    // ---------- create ----------
    @Test
    void create_emailNuevo_creaInactivoConTokenYEnviaCorreo() {
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(roleRepository.findById(3L)).thenReturn(Optional.of(rol(3L, "RECEPTIONIST")));
        saveAsignaId();

        UserResponse response = service.create(new CreateUserRequest("ana@mail.com", 3L));

        assertFalse(response.active());
        assertTrue(response.activationPending());

        ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userRepository).save(captor.capture());
        UserEntity guardado = captor.getValue();
        assertNotNull(guardado.getActivationToken());
        assertTrue(guardado.getActivationTokenExpiresAt().isAfter(LocalDateTime.now().plusHours(23)));
        verify(emailService).sendActivationEmail("ana@mail.com", guardado.getActivationToken());
    }

    @Test
    void create_emailDuplicado_lanzaErrorSinEnviarCorreo() {
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(new CreateUserRequest("ana@mail.com", 3L)));

        assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
        verify(emailService, never()).sendActivationEmail(anyString(), anyString());
    }

    @Test
    void create_rolNoExiste_lanzaRolNoEncontrado() {
        when(userRepository.existsByEmail("ana@mail.com")).thenReturn(false);
        when(roleRepository.findById(9L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.create(new CreateUserRequest("ana@mail.com", 9L)));

        assertEquals(AuthErrorCode.ROLE_NOT_FOUND.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
    }

    // ---------- createDoctor ----------
    @Test
    void createDoctor_creaUsuarioConRolDoctorYEnviaCorreo() {
        when(userRepository.existsByEmail("luis@hospital.com")).thenReturn(false);
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.of(rol(2L, "DOCTOR")));
        saveAsignaId();

        UserResponse response = service.createDoctor(new CreateDoctorRequest("luis@hospital.com"));

        assertEquals(20L, response.id());
        assertEquals("DOCTOR", response.role());
        verify(emailService).sendActivationEmail(eq("luis@hospital.com"), anyString());
    }

    @Test
    void createDoctor_sinRolDoctor_lanzaRolNoEncontrado() {
        when(userRepository.existsByEmail("luis@hospital.com")).thenReturn(false);
        when(roleRepository.findByName("DOCTOR")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createDoctor(new CreateDoctorRequest("luis@hospital.com")));

        assertEquals(AuthErrorCode.ROLE_NOT_FOUND.toString(), ex.getCode());
    }

    @Test
    void createDoctor_emailDuplicado_lanzaError() {
        when(userRepository.existsByEmail("luis@hospital.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.createDoctor(new CreateDoctorRequest("luis@hospital.com")));

        assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
    }

    // ---------- update ----------
    @Test
    void update_mismoRolSinPassword_noRevocaSesiones() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("DOCTOR", true)));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(rol(2L, "DOCTOR")));
        saveAsignaId();

        // Mismo email con otras mayúsculas: no cuenta como cambio, no consulta duplicados
        UserResponse response = service.update(20L, new UpdateUserRequest("LUIS@hospital.com", "", 2L));

        assertEquals("LUIS@hospital.com", response.email());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(refreshTokenService, never()).revokeAllByUserId(anyLong());
    }

    @Test
    void update_conPassword_laEncriptaYRevocaSesiones() {
        UserEntity user = usuario("DOCTOR", true);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(rol(2L, "DOCTOR")));
        when(passwordEncoder.encode("Nueva123!")).thenReturn("$2a$nuevo");
        saveAsignaId();

        service.update(20L, new UpdateUserRequest("luis@hospital.com", "Nueva123!", 2L));

        assertEquals("$2a$nuevo", user.getPassword()); // nunca se guarda en claro
        verify(refreshTokenService).revokeAllByUserId(20L);
    }

    @Test
    void update_cambioDeRol_revocaSesiones() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("DOCTOR", true)));
        when(roleRepository.findById(3L)).thenReturn(Optional.of(rol(3L, "RECEPTIONIST")));
        saveAsignaId();

        UserResponse response = service.update(20L, new UpdateUserRequest("luis@hospital.com", null, 3L));

        assertEquals("RECEPTIONIST", response.role());
        verify(refreshTokenService).revokeAllByUserId(20L);
    }

    @Test
    void update_emailDeOtroUsuario_lanzaErrorYNoGuarda() {
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("DOCTOR", true)));
        when(roleRepository.findById(2L)).thenReturn(Optional.of(rol(2L, "DOCTOR")));
        when(userRepository.existsByEmail("otro@mail.com")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.update(20L, new UpdateUserRequest("otro@mail.com", null, 2L)));

        assertEquals(AuthErrorCode.EMAIL_ALREADY_EXISTS.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
    }

    // ---------- delete (desactivar) ----------
    @Test
    void delete_otroUsuario_loDesactivaYRevocaSesiones() {
        UserContextHolder.set(new UserContext(1L, "ADMIN", Set.of()));
        UserEntity user = usuario("DOCTOR", true);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));

        service.delete(20L);

        assertFalse(user.isActive());
        verify(userRepository).save(user);
        verify(refreshTokenService).revokeAllByUserId(20L);
    }

    @Test
    void delete_aSiMismo_lanzaForbidden() {
        UserContextHolder.set(new UserContext(20L, "ADMIN", Set.of()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("DOCTOR", true)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(20L));

        assertEquals(AuthErrorCode.USER_CANNOT_DEACTIVATE_SELF.toString(), ex.getCode());
        assertEquals(403, ex.getStatus());
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_usuarioAdmin_lanzaForbidden() {
        UserContextHolder.set(new UserContext(1L, "ADMIN", Set.of()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("ADMIN", true)));

        BusinessException ex = assertThrows(BusinessException.class, () -> service.delete(20L));

        assertEquals(AuthErrorCode.USER_ADMIN_CANNOT_BE_DEACTIVATED.toString(), ex.getCode());
        verify(refreshTokenService, never()).revokeAllByUserId(anyLong());
    }

    // ---------- activateByToken ----------
    @Test
    void activateByToken_tokenVigente_activaYGuardaPasswordEncriptada() {
        UserEntity user = usuario("DOCTOR", false);
        user.setActivationToken("tok");
        user.setActivationTokenExpiresAt(LocalDateTime.now().plusHours(1));
        when(userRepository.findByActivationToken("tok")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("Clave123!")).thenReturn("$2a$clave");

        service.activateByToken(new ActivateUserRequest("tok", "Clave123!"));

        assertTrue(user.isActive());
        assertNull(user.getActivationToken()); // el enlace no se puede reutilizar
        assertEquals("$2a$clave", user.getPassword());
        verify(userRepository).save(user);
    }

    @Test
    void activateByToken_tokenVencido_lanzaExpirado() {
        UserEntity user = usuario("DOCTOR", false);
        user.setActivationToken("tok");
        user.setActivationTokenExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(userRepository.findByActivationToken("tok")).thenReturn(Optional.of(user));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.activateByToken(new ActivateUserRequest("tok", "Clave123!")));

        assertEquals(AuthErrorCode.ACTIVATION_TOKEN_EXPIRED.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
    }

    @Test
    void activateByToken_tokenNoExiste_lanzaTokenInvalido() {
        when(userRepository.findByActivationToken("x")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.activateByToken(new ActivateUserRequest("x", "Clave123!")));

        assertEquals(AuthErrorCode.INVALID_ACTIVATION_TOKEN.toString(), ex.getCode());
    }

    // ---------- resendActivation ----------
    @Test
    void resendActivation_usuarioInactivo_generaNuevoTokenYReenvia() {
        UserEntity user = usuario("DOCTOR", false);
        user.setActivationToken("viejo");
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.of(user));
        saveAsignaId();

        service.resendActivation("luis@hospital.com");

        assertNotNull(user.getActivationToken());
        assertFalse("viejo".equals(user.getActivationToken()));
        verify(emailService).sendActivationEmail("luis@hospital.com", user.getActivationToken());
    }

    @Test
    void resendActivation_usuarioYaActivo_lanzaErrorSinEnviar() {
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.of(usuario("DOCTOR", true)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resendActivation("luis@hospital.com"));

        assertEquals(AuthErrorCode.USER_ALREADY_ACTIVE.toString(), ex.getCode());
        verify(emailService, never()).sendActivationEmail(anyString(), anyString());
    }

    @Test
    void resendActivation_emailNoExiste_lanzaNotFound() {
        when(userRepository.findByEmail("x@mail.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.resendActivation("x@mail.com"));

        assertEquals(AuthErrorCode.USER_NOT_FOUND.toString(), ex.getCode());
    }

    // ---------- changePasswordMe ----------
    @Test
    void changePasswordMe_passwordActualCorrecta_cambiaYRevocaSesiones() {
        UserContextHolder.set(new UserContext(20L, "DOCTOR", Set.of()));
        UserEntity user = usuario("DOCTOR", true);
        when(userRepository.findById(20L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Actual1!", "$2a$hash")).thenReturn(true);
        when(passwordEncoder.encode("Nueva123!")).thenReturn("$2a$nueva");

        service.changePasswordMe(new ChangePasswordRequest("Actual1!", "Nueva123!"));

        assertEquals("$2a$nueva", user.getPassword());
        verify(refreshTokenService).revokeAllByUserId(20L);
    }

    @Test
    void changePasswordMe_passwordActualIncorrecta_lanzaErrorYNoGuarda() {
        UserContextHolder.set(new UserContext(20L, "DOCTOR", Set.of()));
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario("DOCTOR", true)));
        when(passwordEncoder.matches("mala", "$2a$hash")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changePasswordMe(new ChangePasswordRequest("mala", "Nueva123!")));

        assertEquals(AuthErrorCode.INVALID_PASSWORD.toString(), ex.getCode());
        verify(userRepository, never()).save(any());
        verify(refreshTokenService, never()).revokeAllByUserId(anyLong());
    }

    @Test
    void changePasswordMe_sinUsuarioLogueado_lanzaNoAutenticado() {
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.changePasswordMe(new ChangePasswordRequest("a", "b")));

        assertEquals(AuthErrorCode.AUTH_INVALID_TOKEN.toString(), ex.getCode());
        assertEquals(401, ex.getStatus());
    }
}
