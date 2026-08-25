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
@RequestMapping("/specialties")
public class SpecialtyController {

    private final IDoctorService doctorService;

    @GetMapping
    public ResponseEntity<Page<SpecialtyResponse>> findAllSpecialties(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                doctorService.findAllSpecialties(pageable)
        );
    }


    @PostMapping
    public ResponseEntity<SpecialtyResponse> createSpecialty(
            @Valid @RequestBody CreateSpecialtyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(doctorService.createSpecialty(request));
    }
}