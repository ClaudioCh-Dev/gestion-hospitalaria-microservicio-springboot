package com.hospital.auth_ms.config;

import java.util.Set;

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

        @Bean
        CommandLineRunner initData(
                        PermissionRepository permissionRepository,
                        RoleRepository roleRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder) {

                return args -> {

                        // =========================
                        // PERMISSIONS
                        // =========================

                        // Patient
                        PermissionEntity patientRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("PATIENT_READ")
                                                        .build());

                        PermissionEntity patientCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("PATIENT_CREATE")
                                                        .build());

                        PermissionEntity patientUpdate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("PATIENT_UPDATE")
                                                        .build());

                        PermissionEntity patientDelete = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("PATIENT_DELETE")
                                                        .build());

                        // Appointment
                        PermissionEntity appointmentCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_CREATE")
                                                        .build());

                        PermissionEntity appointmentRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_READ")
                                                        .build());

                        PermissionEntity appointmentReadByPatient = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_READ_BY_PATIENT")
                                                        .build());

                        PermissionEntity appointmentReadByDoctor = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_READ_BY_DOCTOR")
                                                        .build());

                        PermissionEntity appointmentUpdateStatus = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_UPDATE_STATUS")
                                                        .build());

                        PermissionEntity appointmentCancel = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_CANCEL")
                                                        .build());

                        // Appointment Type
                        PermissionEntity appointmentTypeCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_TYPE_CREATE")
                                                        .build());

                        PermissionEntity appointmentTypeRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_TYPE_READ")
                                                        .build());

                        PermissionEntity appointmentTypeUpdate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_TYPE_UPDATE")
                                                        .build());

                        PermissionEntity appointmentTypeDelete = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("APPOINTMENT_TYPE_DELETE")
                                                        .build());

                        // Billing
                        PermissionEntity billingCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("BILLING_CREATE")
                                                        .build());

                        PermissionEntity billingRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("BILLING_READ")
                                                        .build());

                        PermissionEntity billingReadByPatient = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("BILLING_READ_BY_PATIENT")
                                                        .build());

                        PermissionEntity billingPay = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("BILLING_PAY")
                                                        .build());

                        // Doctor
                        PermissionEntity doctorRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("DOCTOR_READ")
                                                        .build());

                        PermissionEntity doctorReadBySpecialty = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("DOCTOR_READ_BY_SPECIALTY")
                                                        .build());

                        PermissionEntity doctorCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("DOCTOR_CREATE")
                                                        .build());

                        PermissionEntity doctorUpdate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("DOCTOR_UPDATE")
                                                        .build());

                        // Specialty
                        PermissionEntity specialtyRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("SPECIALTY_READ")
                                                        .build());

                        PermissionEntity specialtyCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("SPECIALTY_CREATE")
                                                        .build());

                        // Medical Record
                        PermissionEntity medicalRecordRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("MEDICAL_RECORD_READ")
                                                        .build());

                        PermissionEntity medicalRecordReadByPatient = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("MEDICAL_RECORD_READ_BY_PATIENT")
                                                        .build());

                        // Notification
                        PermissionEntity notificationCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("NOTIFICATION_CREATE")
                                                        .build());

                        PermissionEntity notificationReadDoctor = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("NOTIFICATION_READ_DOCTOR")
                                                        .build());

                        PermissionEntity notificationReadAdmin = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("NOTIFICATION_READ_ADMIN")
                                                        .build());

                        PermissionEntity notificationMarkReadDoctor = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("NOTIFICATION_MARK_READ_DOCTOR")
                                                        .build());

                        PermissionEntity notificationMarkReadAdmin = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("NOTIFICATION_MARK_READ_ADMIN")
                                                        .build());
                        
                        //Users
                        PermissionEntity userRead = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("USER_READ")
                                                        .build());
                        
                        PermissionEntity userCreate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("USER_CREATE")
                                                        .build());
                        
                        PermissionEntity userUpdate = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("USER_UPDATE")
                                                        .build());
                        
                        PermissionEntity userDelete = permissionRepository.save(
                                        PermissionEntity.builder()
                                                        .name("USER_DELETE")
                                                        .build());

                        // =========================
                        // ROLES
                        // =========================

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

                        // =========================
                        // USERS
                        // =========================

                        userRepository.save(
                                        UserEntity.builder()
                                                        .username("admin")
                                                        .password(
                                                                        passwordEncoder.encode("123456"))
                                                        .role(admin)
                                                        .build());

                        userRepository.save(
                                        UserEntity.builder()
                                                        .username("doctor")
                                                        .password(
                                                                        passwordEncoder.encode("123456"))
                                                        .role(doctor)
                                                        .build());
                };
        }
}