package com.hospital.auth_ms.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.hospital.auth_ms.dtos.users.ActivateUserRequest;
import com.hospital.auth_ms.dtos.users.ChangePasswordRequest;
import com.hospital.auth_ms.dtos.users.CreateDoctorRequest;
import com.hospital.auth_ms.dtos.users.CreateUserRequest;
import com.hospital.auth_ms.dtos.users.ResendActivationRequest;
import com.hospital.auth_ms.dtos.users.UpdateUserRequest;
import com.hospital.auth_ms.dtos.users.UserResponse;
import com.hospital.auth_ms.services.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @PreAuthorize("@auth.hasPermission('USER_READ')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @PreAuthorize("@auth.hasPermission('USER_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userService.findById(id));
    }

    @PreAuthorize("@auth.hasPermission('USER_CREATE')")
    @PostMapping
    public ResponseEntity<UserResponse> create(
            @RequestBody CreateUserRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.create(request));
    }

    @PreAuthorize("@auth.hasPermission('USER_UPDATE')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(
                userService.update(id, request));
    }

    @PreAuthorize("@auth.hasPermission('USER_DELETE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        userService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@auth.hasPermission('USER_CREATE')")
    @PostMapping("/doctor")
    public ResponseEntity<UserResponse> createDoctor(
            @RequestBody CreateDoctorRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userService.createDoctor(request));
    }

    /*
     * /
     * 
     * @PreAuthorize("@auth.hasPermission('USER_UPDATE')")
     * 
     * @PatchMapping("/{id}/activate")
     * public ResponseEntity<Void> activate(
     * 
     * @PathVariable Long id) {
     * 
     * userService.activate(id);
     * 
     * return ResponseEntity.noContent().build();
     * }
     */

    @PostMapping("/activate")
    public ResponseEntity<Void> activateByToken(
            @RequestBody ActivateUserRequest request) {

        userService.activateByToken(request);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("@auth.hasPermission('USER_UPDATE')")
    @PostMapping("/resend-activation")
    public ResponseEntity<Void> resendActivation(
            @Valid @RequestBody ResendActivationRequest request) {

        userService.resendActivation(request.email());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password-me")
    public ResponseEntity<Void> changePasswordMe(
            @Valid @RequestBody ChangePasswordRequest request) {

        userService.changePasswordMe(request);

        return ResponseEntity.noContent().build();
    }
}