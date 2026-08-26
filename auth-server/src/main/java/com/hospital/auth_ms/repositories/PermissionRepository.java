package com.hospital.auth_ms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hospital.auth_ms.entities.PermissionEntity;

public interface PermissionRepository extends JpaRepository<PermissionEntity, Long> {

}