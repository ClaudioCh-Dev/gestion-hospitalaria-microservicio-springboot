package com.hospital.auth_ms.services.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.hospital.auth_ms.dtos.users.CreateUserRequest;
import com.hospital.auth_ms.dtos.users.UpdateUserRequest;
import com.hospital.auth_ms.dtos.users.UserResponse;
import com.hospital.auth_ms.entities.RoleEntity;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.repositories.RoleRepository;
import com.hospital.auth_ms.repositories.UserRepository;
import com.hospital.auth_ms.services.IUserService;
import com.hospital.auth_ms.services.IRefreshTokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final IRefreshTokenService refreshTokenService;

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

        UserEntity user = UserEntity.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(role)
                .active(true)
                .build();

        return toResponse(userRepository.save(user));
    }

    @Override
    public UserResponse update(Long id, UpdateUserRequest request) {

        UserEntity user = findUser(id);

        RoleEntity role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        user.setUsername(request.username());
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

        user.setActive(false);

        userRepository.save(user);

        refreshTokenService.revokeAllByUserId(id);
    }

    @Override
    public void activate(Long id) {

        UserEntity user = findUser(id);

        user.setActive(true);

        userRepository.save(user);
    }

    private UserEntity findUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    private UserResponse toResponse(UserEntity user) {

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole().getId(),
                user.getRole().getName(),
                user.isActive());
    }
}