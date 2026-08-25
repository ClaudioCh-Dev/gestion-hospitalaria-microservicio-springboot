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
@RequiredArgsConstructor
@RequestMapping("/crud")
public class DoctorController {

    private final IDoctorService doctorService;


    @GetMapping()
    public ResponseEntity<Page<DoctorResponse>> findAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findAll(pageable)
        );
    }


    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> findById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                doctorService.findById(id)
        );
    }


    @GetMapping("/specialty/{specialtyId}")
    public ResponseEntity<Page<DoctorResponse>> findBySpecialty(
            @PathVariable Long specialtyId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findBySpecialty(specialtyId, pageable)
        );
    }


    @PostMapping()
    public ResponseEntity<DoctorResponse> create(
            @Valid @RequestBody CreateDoctorRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.create(request));
    }


    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateDoctorRequest request
    ) {
        return ResponseEntity.ok(
                doctorService.update(id, request)
        );
    }

}