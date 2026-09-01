package com.hospital.auth_ms.config;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.hospital.auth_ms.entities.PermissionEntity;
import com.hospital.auth_ms.entities.RoleEntity;
import com.hospital.auth_ms.entities.UserEntity;
import com.hospital.auth_ms.repositories.PermissionRepository;
import com.hospital.auth_ms.repositories.RoleRepository;
import com.hospital.auth_ms.repositories.UserRepository;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner initData(
            PermissionRepository permissionRepository,
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            log.info("==========================================");
            log.info("INICIANDO DATA SEEDER");
            log.info("==========================================");

            try {

                // =========================
                // PERMISSIONS
                // =========================

                log.info("Creando permisos...");

                PermissionEntity patientRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("PATIENT_READ")
                                .build());

                log.info("Permiso creado: PATIENT_READ");

                PermissionEntity patientCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("PATIENT_CREATE")
                                .build());

                log.info("Permiso creado: PATIENT_CREATE");

                PermissionEntity patientUpdate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("PATIENT_UPDATE")
                                .build());

                log.info("Permiso creado: PATIENT_UPDATE");

                PermissionEntity patientDelete = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("PATIENT_DELETE")
                                .build());

                log.info("Permiso creado: PATIENT_DELETE");

                PermissionEntity appointmentCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_CREATE")
                                .build());

                log.info("Permiso creado: APPOINTMENT_CREATE");

                PermissionEntity appointmentRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_READ")
                                .build());

                log.info("Permiso creado: APPOINTMENT_READ");

                PermissionEntity appointmentReadByPatient = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_READ_BY_PATIENT")
                                .build());

                log.info("Permiso creado: APPOINTMENT_READ_BY_PATIENT");

                PermissionEntity appointmentReadByDoctor = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_READ_BY_DOCTOR")
                                .build());

                log.info("Permiso creado: APPOINTMENT_READ_BY_DOCTOR");

                PermissionEntity appointmentUpdateStatus = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_UPDATE_STATUS")
                                .build());

                log.info("Permiso creado: APPOINTMENT_UPDATE_STATUS");

                PermissionEntity appointmentCancel = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_CANCEL")
                                .build());

                log.info("Permiso creado: APPOINTMENT_CANCEL");

                PermissionEntity appointmentTypeCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_TYPE_CREATE")
                                .build());

                log.info("Permiso creado: APPOINTMENT_TYPE_CREATE");

                PermissionEntity appointmentTypeRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_TYPE_READ")
                                .build());

                log.info("Permiso creado: APPOINTMENT_TYPE_READ");

                PermissionEntity appointmentTypeUpdate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_TYPE_UPDATE")
                                .build());

                log.info("Permiso creado: APPOINTMENT_TYPE_UPDATE");

                PermissionEntity appointmentTypeDelete = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("APPOINTMENT_TYPE_DELETE")
                                .build());

                log.info("Permiso creado: APPOINTMENT_TYPE_DELETE");

                PermissionEntity billingCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("BILLING_CREATE")
                                .build());

                log.info("Permiso creado: BILLING_CREATE");

                PermissionEntity billingRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("BILLING_READ")
                                .build());

                log.info("Permiso creado: BILLING_READ");

                PermissionEntity billingReadByPatient = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("BILLING_READ_BY_PATIENT")
                                .build());

                log.info("Permiso creado: BILLING_READ_BY_PATIENT");

                PermissionEntity billingPay = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("BILLING_PAY")
                                .build());

                log.info("Permiso creado: BILLING_PAY");

                PermissionEntity doctorRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("DOCTOR_READ")
                                .build());

                log.info("Permiso creado: DOCTOR_READ");

                PermissionEntity doctorReadBySpecialty = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("DOCTOR_READ_BY_SPECIALTY")
                                .build());

                log.info("Permiso creado: DOCTOR_READ_BY_SPECIALTY");

                PermissionEntity doctorCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("DOCTOR_CREATE")
                                .build());

                log.info("Permiso creado: DOCTOR_CREATE");

                PermissionEntity doctorUpdate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("DOCTOR_UPDATE")
                                .build());

                log.info("Permiso creado: DOCTOR_UPDATE");

                PermissionEntity specialtyRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("SPECIALTY_READ")
                                .build());

                log.info("Permiso creado: SPECIALTY_READ");

                PermissionEntity specialtyCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("SPECIALTY_CREATE")
                                .build());

                log.info("Permiso creado: SPECIALTY_CREATE");

                PermissionEntity medicalRecordRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("MEDICAL_RECORD_READ")
                                .build());

                log.info("Permiso creado: MEDICAL_RECORD_READ");

                PermissionEntity medicalRecordReadByPatient = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("MEDICAL_RECORD_READ_BY_PATIENT")
                                .build());

                log.info("Permiso creado: MEDICAL_RECORD_READ_BY_PATIENT");

                PermissionEntity notificationCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("NOTIFICATION_CREATE")
                                .build());

                log.info("Permiso creado: NOTIFICATION_CREATE");

                PermissionEntity notificationReadDoctor = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("NOTIFICATION_READ_DOCTOR")
                                .build());

                log.info("Permiso creado: NOTIFICATION_READ_DOCTOR");

                PermissionEntity notificationReadAdmin = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("NOTIFICATION_READ_ADMIN")
                                .build());

                log.info("Permiso creado: NOTIFICATION_READ_ADMIN");

                PermissionEntity notificationMarkReadDoctor = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("NOTIFICATION_MARK_READ_DOCTOR")
                                .build());

                log.info("Permiso creado: NOTIFICATION_MARK_READ_DOCTOR");

                PermissionEntity notificationMarkReadAdmin = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("NOTIFICATION_MARK_READ_ADMIN")
                                .build());

                log.info("Permiso creado: NOTIFICATION_MARK_READ_ADMIN");

                PermissionEntity userRead = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("USER_READ")
                                .build());

                log.info("Permiso creado: USER_READ");

                PermissionEntity userCreate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("USER_CREATE")
                                .build());

                log.info("Permiso creado: USER_CREATE");

                PermissionEntity userUpdate = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("USER_UPDATE")
                                .build());

                log.info("Permiso creado: USER_UPDATE");

                PermissionEntity userDelete = permissionRepository.save(
                        PermissionEntity.builder()
                                .name("USER_DELETE")
                                .build());

                log.info("Permiso creado: USER_DELETE");

                log.info("TODOS LOS PERMISOS CREADOS");

                // =========================
                // ROLES
                // =========================

                log.info("Creando rol ADMIN...");

                RoleEntity admin = roleRepository.save(
                        RoleEntity.builder()
                                .name("ADMIN")
                                .permissions(Set.of(
                                        patientRead,
                                        patientCreate,
                                        patientUpdate,
                                        patientDelete,
                                        appointmentCreate,
                                        appointmentRead,
                                        appointmentReadByPatient,
                                        appointmentReadByDoctor,
                                        appointmentUpdateStatus,
                                        appointmentCancel,
                                        appointmentTypeCreate,
                                        appointmentTypeRead,
                                        appointmentTypeUpdate,
                                        appointmentTypeDelete,
                                        billingCreate,
                                        billingRead,
                                        billingReadByPatient,
                                        billingPay,
                                        doctorRead,
                                        doctorReadBySpecialty,
                                        doctorCreate,
                                        doctorUpdate,
                                        specialtyRead,
                                        specialtyCreate,
                                        medicalRecordRead,
                                        medicalRecordReadByPatient,
                                        notificationCreate,
                                        notificationReadAdmin,
                                        notificationMarkReadAdmin,
                                        userRead,
                                        userCreate,
                                        userUpdate,
                                        userDelete))
                                .build());

                log.info("ROL ADMIN CREADO - ID: {}", admin.getId());

                log.info("Creando rol DOCTOR...");

                RoleEntity doctor = roleRepository.save(
                        RoleEntity.builder()
                                .name("DOCTOR")
                                .permissions(Set.of(
                                        patientRead,
                                        appointmentRead,
                                        appointmentReadByDoctor,
                                        appointmentUpdateStatus,
                                        appointmentTypeRead,
                                        doctorRead,
                                        doctorReadBySpecialty,
                                        specialtyRead,
                                        medicalRecordRead,
                                        medicalRecordReadByPatient,
                                        notificationReadDoctor,
                                        notificationMarkReadDoctor))
                                .build());

                log.info("ROL DOCTOR CREADO - ID: {}", doctor.getId());

                // =========================
                // USERS
                // =========================

                log.info("Creando usuario admin...");

                UserEntity adminUser = userRepository.save(
                        UserEntity.builder()
                                .email("admin@example.com")
                                .password(passwordEncoder.encode("123456"))
                                .role(admin)
                                .active(true)
                                .build());

                log.info(
                        "USUARIO ADMIN CREADO - ID: {}, EMAIL: {}",
                        adminUser.getId(),
                        adminUser.getEmail());

                log.info("Creando usuario doctor...");

                UserEntity doctorUser = userRepository.save(
                        UserEntity.builder()
                                .email("doctor@example.com")
                                .password(passwordEncoder.encode("123456"))
                                .role(doctor)
                                .active(true)
                                .build());

                log.info(
                        "USUARIO DOCTOR CREADO - ID: {}, EMAIL: {}",
                        doctorUser.getId(),
                        doctorUser.getEmail());

                log.info("==========================================");
                log.info("DATA SEEDER FINALIZADO CORRECTAMENTE");
                log.info("==========================================");

            } catch (Exception e) {

                log.error("==========================================");
                log.error("ERROR EJECUTANDO DATA SEEDER");
                log.error("==========================================", e);

                throw e;
            }
        };
    }
}
