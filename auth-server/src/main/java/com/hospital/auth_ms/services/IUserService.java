package com.hospital.auth_ms.services;

import java.util.List;

import com.hospital.auth_ms.dtos.users.ActivateUserRequest;
import com.hospital.auth_ms.dtos.users.ChangePasswordRequest;
import com.hospital.auth_ms.dtos.users.CreateDoctorRequest;
import com.hospital.auth_ms.dtos.users.CreateUserRequest;
import com.hospital.auth_ms.dtos.users.UpdateUserRequest;
import com.hospital.auth_ms.dtos.users.UserResponse;

public interface IUserService {

    List<UserResponse> findAll();

    UserResponse findById(Long id);

    UserResponse create(CreateUserRequest request);

    UserResponse update(Long id, UpdateUserRequest request);
    
    UserResponse createDoctor(CreateDoctorRequest request);

    void changePasswordMe(ChangePasswordRequest request);

    void delete(Long id);

    //void activate(Long id);
    
    void activateByToken(ActivateUserRequest request);

    void resendActivation(String email);
}
