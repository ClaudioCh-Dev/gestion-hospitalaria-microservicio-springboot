package com.hospital.auth_ms.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.dtos.ClaimsDto;
import com.hospital.auth_ms.dtos.RefreshTokenDto;
import com.hospital.auth_ms.dtos.TokenDto;
import com.hospital.auth_ms.dtos.UserDto;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.helpers.JwtHelper;
import com.hospital.auth_ms.repositories.UserRepository;

import personal.shared.exception.BusinessException;

import jakarta.transaction.Transactional;

import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class AuthServiceImpl implements AuthService {

        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtHelper jwtHelper;
        private final RefreshTokenService refreshTokenService;

        @Override
        public TokenDto login(UserDto userDto) {

                final var userFromDb = this.userRepository
                                .findByUsername(userDto.getUsername())
                                .orElseThrow(() -> new BusinessException(
                                                AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                                                "Credenciales inválidas"));

                this.validPassword(userDto, userFromDb);

                String accessToken = this.jwtHelper.createToken(
                                ClaimsDto.builder()
                                                .userId(userFromDb.getId())
                                                .username(userFromDb.getUsername())
                                                .role(userFromDb.getRole())
                                                .build());

                String refreshToken = this.refreshTokenService.createRefreshToken(
                                userFromDb.getId());

                return TokenDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        @Override
        public ClaimsDto validateToken(String accessToken) {

                if (!this.jwtHelper.validateToken(accessToken)) {
                        throw new BusinessException(
                                        AuthErrorCode.AUTH_INVALID_TOKEN,
                                        "Token inválido");
                }

                return ClaimsDto.builder()
                                .userId(
                                                this.jwtHelper.getUserIdFromToken(accessToken))
                                .username(
                                                this.jwtHelper.getUsernameFromToken(accessToken))
                                .role(
                                                this.jwtHelper.getRoleFromToken(accessToken))
                                .build();
        }

        private void validPassword(
                        UserDto userDto,
                        UserEntity userEntity) {

                if (!this.passwordEncoder.matches(
                                userDto.getPassword(),
                                userEntity.getPassword())) {
                        throw new BusinessException(
                                        AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                                        "Credenciales inválidas");
                }
        }

        @Override
        public TokenDto refreshToken(RefreshTokenDto refreshTokenDto) {

                String oldRefreshToken = refreshTokenDto.refreshToken();

                Long userId = this.refreshTokenService
                                .validateRefreshToken(oldRefreshToken);

                final var userFromDb = this.userRepository
                                .findById(userId)
                                .orElseThrow(() -> new BusinessException(
                                                AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                                                "Usuario no encontrado"));

                // Revocar refresh token anterior
                this.refreshTokenService.revokeRefreshToken(oldRefreshToken);

                // Crear nuevo refresh token
                String newRefreshToken = this.refreshTokenService.createRefreshToken(userId);

                // Crear nuevo access token
                String accessToken = this.jwtHelper.createToken(
                                ClaimsDto.builder()
                                                .userId(userFromDb.getId())
                                                .username(userFromDb.getUsername())
                                                .role(userFromDb.getRole())
                                                .build());

                return TokenDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(newRefreshToken)
                                .build();
        }
}