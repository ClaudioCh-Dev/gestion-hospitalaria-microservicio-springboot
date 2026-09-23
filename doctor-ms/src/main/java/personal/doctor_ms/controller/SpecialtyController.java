package personal.doctor_ms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import personal.doctor_ms.dtos.*;
import personal.doctor_ms.service.IDoctorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/specialties")
public class SpecialtyController {

    private final IDoctorService doctorService;

    @GetMapping
    @PreAuthorize("@auth.hasPermission('SPECIALTY_READ')")
    public ResponseEntity<List<SpecialtyResponse>> findAllSpecialties() {
        return ResponseEntity.ok(
                doctorService.findAllSpecialties()
        );
    }

    @PostMapping
    @PreAuthorize("@auth.hasPermission('SPECIALTY_CREATE')")
    public ResponseEntity<SpecialtyResponse> createSpecialty(
            @Valid @RequestBody CreateSpecialtyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createSpecialty(request));
    }
}