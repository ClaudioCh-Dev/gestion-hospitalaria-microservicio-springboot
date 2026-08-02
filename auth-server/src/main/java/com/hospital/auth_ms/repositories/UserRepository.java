package com.hospital.auth_ms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.auth_ms.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsername(String username);
}
