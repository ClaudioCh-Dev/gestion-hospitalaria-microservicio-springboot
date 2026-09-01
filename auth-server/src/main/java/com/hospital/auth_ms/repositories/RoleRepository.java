package com.hospital.auth_ms.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.auth_ms.entities.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    Optional<RoleEntity> findByName(String name);
}
