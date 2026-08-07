package personal.doctor_ms.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import personal.doctor_ms.dtos.*;
import personal.doctor_ms.service.IDoctorService;

@RestController
@RequestMapping("/doctor-ms")
@RequiredArgsConstructor
public class DoctorController {

    private final IDoctorService doctorService;


    @GetMapping("/doctors")
    public ResponseEntity<Page<DoctorResponse>> findAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findAll(pageable)
        );
    }


    @GetMapping("/doctors/{id}")
    public ResponseEntity<DoctorResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                doctorService.findById(id)
        );
    }


    @GetMapping("/doctors/specialty/{specialtyId}")
    public ResponseEntity<Page<DoctorResponse>> findBySpecialty(
            @PathVariable Long specialtyId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findBySpecialty(specialtyId, pageable)
        );
    }


    @PostMapping("/doctors")
    public ResponseEntity<DoctorResponse> create(
            @Valid @RequestBody CreateDoctorRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.create(request));
    }


    @PutMapping("/doctors/{id}")
    public ResponseEntity<DoctorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorRequest request
    ) {
        return ResponseEntity.ok(
                doctorService.update(id, request)
        );
    }


    @GetMapping("/specialties")
    public ResponseEntity<Page<SpecialtyResponse>> findAllSpecialties(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findAllSpecialties(pageable)
        );
    }


    @PostMapping("/specialties")
    public ResponseEntity<SpecialtyResponse> createSpecialty(
            @Valid @RequestBody CreateSpecialtyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createSpecialty(request));
    }

}