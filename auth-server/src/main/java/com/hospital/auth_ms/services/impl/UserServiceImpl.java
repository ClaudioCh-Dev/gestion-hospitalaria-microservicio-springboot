package com.hospital.auth_ms.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.dtos.users.ActivateUserRequest;
import com.hospital.auth_ms.dtos.users.ChangePasswordRequest;
import com.hospital.auth_ms.dtos.users.CreateDoctorRequest;
import com.hospital.auth_ms.dtos.users.CreateUserRequest;
import com.hospital.auth_ms.dtos.users.UpdateUserRequest;
import com.hospital.auth_ms.dtos.users.UserResponse;
import com.hospital.auth_ms.entities.RoleEntity;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.exceptions.AuthErrorCode;
import com.hospital.auth_ms.repositories.RoleRepository;
import com.hospital.auth_ms.repositories.UserRepository;
import com.hospital.auth_ms.security.UserContext;
import com.hospital.auth_ms.security.UserContextHolder;
import com.hospital.auth_ms.services.IUserService;
import com.hospital.auth_ms.services.IEmailService;
import com.hospital.auth_ms.services.IRefreshTokenService;

import lombok.RequiredArgsConstructor;
import personal.shared.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;
    private final IEmailService emailService;

    @Override
    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long id) {
        return toResponse(findUser(id));
    }

    @Override
    public UserResponse create(CreateUserRequest request) {

        RoleEntity role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        String activationToken = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .role(role)
                .active(false)
                .activationToken(activationToken)
                .activationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        UserEntity savedUser = userRepository.save(user);

        emailService.sendActivationEmail(
                savedUser.getEmail(),
                savedUser.getActivationToken());

        return toResponse(savedUser);
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {

        UserEntity user = findUser(id);

        RoleEntity role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.setEmail(request.email());
        user.setRole(role);

        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(
                    passwordEncoder.encode(request.password()));
        }

        return toResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long id) {

        UserEntity user = findUser(id);

        if ("ADMIN".equals(user.getRole().getName())) {
            throw new BusinessException(
                    AuthErrorCode.USER_ADMIN_CANNOT_BE_DEACTIVATED,
                    "No se puede desactivar un usuario con rol ADMIN");
        }

        user.setActive(false);

        userRepository.save(user);

        refreshTokenService.revokeAllByUserId(id);
    }

    @Override
    public void activateByToken(ActivateUserRequest request) {

        UserEntity user = userRepository.findByActivationToken(request.token())
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.INVALID_ACTIVATION_TOKEN,
                        "Token de activación inválido"));

        if (user.getActivationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException(
                    AuthErrorCode.ACTIVATION_TOKEN_EXPIRED,
                    "El token de activación ha expirado");
        }

        user.setActive(true);
        user.setActivationToken(null);
        user.setActivationTokenExpiresAt(null);
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
    }

    @Override
    public void resendActivation(String email) {

        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(
                        AuthErrorCode.USER_NOT_FOUND,
                        "Usuario no encontrado"));

        if (user.isActive()) {
            throw new BusinessException(
                    AuthErrorCode.USER_ALREADY_ACTIVE,
                    "El usuario ya está activo");
        }

        String activationToken = UUID.randomUUID().toString();

        user.setActivationToken(activationToken);
        user.setActivationTokenExpiresAt(
                LocalDateTime.now().plusHours(24));

        UserEntity savedUser = userRepository.save(user);

        emailService.sendActivationEmail(
                savedUser.getEmail(),
                savedUser.getActivationToken());
    }

    @Override
    public UserResponse createDoctor(CreateDoctorRequest request) {

        RoleEntity role = roleRepository.findByName("DOCTOR")
                .orElseThrow(() -> new RuntimeException("Rol DOCTOR no encontrado"));

        String activationToken = UUID.randomUUID().toString();

        UserEntity user = UserEntity.builder()
                .email(request.email())
                .role(role)
                .active(false)
                .activationToken(activationToken)
                .activationTokenExpiresAt(LocalDateTime.now().plusHours(24))
                .build();

        UserEntity savedUser = userRepository.save(user);

        emailService.sendActivationEmail(
                savedUser.getEmail(),
                savedUser.getActivationToken());

        return toResponse(savedUser);
    }

    @Override
    public void changePasswordMe(ChangePasswordRequest request) {

        UserContext context = UserContextHolder.get();

        UserEntity user = findUser(context.userId());

        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPassword())) {

            throw new BusinessException(
                    AuthErrorCode.INVALID_PASSWORD,
                    "La contraseña actual es incorrecta");
        }

        user.setPassword(
                passwordEncoder.encode(request.newPassword()));

        userRepository.save(user);

        refreshTokenService.revokeAllByUserId(user.getId());
    }

    private UserEntity findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UserResponse toResponse(UserEntity user) {

        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.isActive());
    }

        /*
     * @Override
     * public void activate(Long id) {
     * 
     * UserEntity user = findUser(id);
     * 
     * user.setActive(true);
     * 
     * userRepository.save(user);
     * }
     */
}