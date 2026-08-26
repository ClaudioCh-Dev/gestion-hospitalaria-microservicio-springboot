package com.hospital.auth_ms.services;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.dtos.AuthTokenDto;
import com.hospital.auth_ms.dtos.ClaimsDto;
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
    public AuthTokenDto login(UserDto userDto) {

        final var userFromDb = this.userRepository
                .findByUsername(userDto.getUsername())
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                        "Credenciales inválidas"
                ));

        this.validPassword(userDto, userFromDb);

        String accessToken = this.jwtHelper.createToken(
                ClaimsDto.builder()
                        .userId(userFromDb.getId())
                        .username(userFromDb.getUsername())
                        .role(userFromDb.getRole().getName())
                        .permissions(getPermissions(userFromDb))
                        .build()
        );

        String refreshToken = this.refreshTokenService
                .createRefreshToken(userFromDb.getId());

        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    @Override
    public ClaimsDto validateToken(String accessToken) {

        if (!this.jwtHelper.validateToken(accessToken)) {
            throw new BusinessException(
                    AuthErrorCode.AUTH_INVALID_TOKEN,
                    "Token inválido"
            );
        }

        return ClaimsDto.builder()
                .userId(
                        this.jwtHelper.getUserIdFromToken(accessToken)
                )
                .username(
                        this.jwtHelper.getUsernameFromToken(accessToken)
                )
                .role(
                        this.jwtHelper.getRoleFromToken(accessToken)
                )
                .permissions(
                        this.jwtHelper.getPermissionsFromToken(accessToken)
                )
                .build();
    }

    @Override
    public AuthTokenDto refreshToken(String oldRefreshToken) {

        Long userId = this.refreshTokenService
                .validateRefreshToken(oldRefreshToken);

        final var userFromDb = this.userRepository
                .findById(userId)
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                        "Usuario no encontrado"
                ));

        this.refreshTokenService
                .revokeRefreshToken(oldRefreshToken);

        String newRefreshToken = this.refreshTokenService
                .createRefreshToken(userId);

        String accessToken = this.jwtHelper.createToken(
                ClaimsDto.builder()
                        .userId(userFromDb.getId())
                        .username(userFromDb.getUsername())
                        .role(userFromDb.getRole().getName())
                        .permissions(getPermissions(userFromDb))
                        .build()
        );

        return AuthTokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    private Set<String> getPermissions(UserEntity user) {

        return user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet());
    }

    private void validPassword(
            UserDto userDto,
            UserEntity userEntity) {

        if (!this.passwordEncoder.matches(
                userDto.getPassword(),
                userEntity.getPassword())) {

            throw new BusinessException(
                    AuthErrorCode.AUTH_INVALID_CREDENTIALS,
                    "Credenciales inválidas"
            );
        }
    }
}