package com.hospital.auth_ms.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.auth_ms.dtos.authentication.AuthTokenDto;
import com.hospital.auth_ms.dtos.authentication.ClaimsDto;
import com.hospital.auth_ms.dtos.authentication.UserDto;
import com.hospital.auth_ms.entities.PermissionEntity;
import com.hospital.auth_ms.entities.RoleEntity;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.helpers.JwtHelper;
import com.hospital.auth_ms.repositories.UserRepository;
import com.hospital.auth_ms.services.IRefreshTokenService;

import personal.shared.exception.BusinessException;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private IRefreshTokenService refreshTokenService;

    @InjectMocks
    private AuthServiceImpl service;

    private UserEntity usuario(boolean active) {
        RoleEntity role = RoleEntity.builder()
                .id(2L)
                .name("DOCTOR")
                .permissions(Set.of(
                        PermissionEntity.builder().id(1L).name("APPOINTMENT_READ_OWN").build(),
                        PermissionEntity.builder().id(2L).name("PATIENT_READ").build()))
                .build();
        return UserEntity.builder()
                .id(20L)
                .email("luis@hospital.com")
                .password("$2a$hash")
                .role(role)
                .active(active)
                .build();
    }

    private UserDto credenciales(String password) {
        return UserDto.builder().email("luis@hospital.com").password(password).build();
    }

    // ---------- login ----------
    @Test
    void login_credencialesValidas_devuelveAccessYRefreshToken() {
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("Secreta1!", "$2a$hash")).thenReturn(true);
        when(jwtHelper.createToken(any(ClaimsDto.class))).thenReturn("access-jwt");
        when(refreshTokenService.createRefreshToken(20L)).thenReturn("refresh-uuid");

        AuthTokenDto tokens = service.login(credenciales("Secreta1!"));

        assertEquals("access-jwt", tokens.getAccessToken());
        assertEquals("refresh-uuid", tokens.getRefreshToken());

        // El JWT se arma con los datos del usuario y los nombres de sus permisos
        ArgumentCaptor<ClaimsDto> captor = ArgumentCaptor.forClass(ClaimsDto.class);
        verify(jwtHelper).createToken(captor.capture());
        assertEquals(20L, captor.getValue().getUserId());
        assertEquals("DOCTOR", captor.getValue().getRole());
        assertEquals(Set.of("APPOINTMENT_READ_OWN", "PATIENT_READ"), captor.getValue().getPermissions());
    }

    @Test
    void login_emailNoExiste_lanzaCredencialesInvalidas() {
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.login(credenciales("Secreta1!")));

        assertEquals(AuthErrorCode.AUTH_INVALID_CREDENTIALS.toString(), ex.getCode());
        assertEquals(401, ex.getStatus());
    }

    @Test
    void login_passwordIncorrecta_lanzaCredencialesInvalidasSinCrearTokens() {
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.of(usuario(true)));
        when(passwordEncoder.matches("mala", "$2a$hash")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.login(credenciales("mala")));

        // Mismo error que "email no existe": no se revela qué dato falló
        assertEquals(AuthErrorCode.AUTH_INVALID_CREDENTIALS.toString(), ex.getCode());
        verify(jwtHelper, never()).createToken(any());
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    @Test
    void login_usuarioInactivo_lanzaUsuarioInactivo() {
        when(userRepository.findByEmail("luis@hospital.com")).thenReturn(Optional.of(usuario(false)));
        when(passwordEncoder.matches("Secreta1!", "$2a$hash")).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.login(credenciales("Secreta1!")));

        assertEquals(AuthErrorCode.AUTH_USER_INACTIVE.toString(), ex.getCode());
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    // ---------- validateToken ----------
    @Test
    void validateToken_tokenValido_devuelveClaims() {
        when(jwtHelper.validateToken("jwt")).thenReturn(true);
        when(jwtHelper.getUserIdFromToken("jwt")).thenReturn(20L);
        when(jwtHelper.getEmailFromToken("jwt")).thenReturn("luis@hospital.com");
        when(jwtHelper.getRoleFromToken("jwt")).thenReturn("DOCTOR");
        when(jwtHelper.getPermissionsFromToken("jwt")).thenReturn(Set.of("PATIENT_READ"));

        ClaimsDto claims = service.validateToken("jwt");

        assertEquals(20L, claims.getUserId());
        assertEquals("DOCTOR", claims.getRole());
        assertEquals(Set.of("PATIENT_READ"), claims.getPermissions());
    }

    @Test
    void validateToken_tokenInvalido_lanzaTokenInvalido() {
        when(jwtHelper.validateToken("malo")).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validateToken("malo"));

        assertEquals(AuthErrorCode.AUTH_INVALID_TOKEN.toString(), ex.getCode());
        verify(jwtHelper, never()).getUserIdFromToken(any());
    }

    // ---------- refreshToken ----------
    @Test
    void refreshToken_valido_rotaElRefreshYCreaNuevoAccess() {
        when(refreshTokenService.validateRefreshToken("viejo")).thenReturn(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario(true)));
        when(refreshTokenService.createRefreshToken(20L)).thenReturn("nuevo");
        when(jwtHelper.createToken(any(ClaimsDto.class))).thenReturn("access-jwt");

        AuthTokenDto tokens = service.refreshToken("viejo");

        assertEquals("nuevo", tokens.getRefreshToken());
        assertEquals("access-jwt", tokens.getAccessToken());
        verify(refreshTokenService).revokeRefreshToken("viejo"); // el viejo ya no sirve
    }

    @Test
    void refreshToken_usuarioDesactivado_revocaYLanzaInactivo() {
        when(refreshTokenService.validateRefreshToken("viejo")).thenReturn(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.of(usuario(false)));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.refreshToken("viejo"));

        assertEquals(AuthErrorCode.AUTH_USER_INACTIVE.toString(), ex.getCode());
        verify(refreshTokenService).revokeRefreshToken("viejo");
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    @Test
    void refreshToken_usuarioYaNoExiste_lanzaCredencialesInvalidas() {
        when(refreshTokenService.validateRefreshToken("viejo")).thenReturn(20L);
        when(userRepository.findById(20L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.refreshToken("viejo"));

        assertEquals(AuthErrorCode.AUTH_INVALID_CREDENTIALS.toString(), ex.getCode());
        verify(refreshTokenService, never()).createRefreshToken(anyLong());
    }

    @Test
    void refreshToken_refreshInvalido_propagaElErrorSinBuscarUsuario() {
        when(refreshTokenService.validateRefreshToken("vencido")).thenThrow(new BusinessException(
                AuthErrorCode.AUTH_INVALID_REFRESH_TOKEN, "Refresh token inválido o expirado"));

        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.refreshToken("vencido"));

        assertEquals(AuthErrorCode.AUTH_INVALID_REFRESH_TOKEN.toString(), ex.getCode());
        verify(userRepository, never()).findById(anyLong());
    }
}
