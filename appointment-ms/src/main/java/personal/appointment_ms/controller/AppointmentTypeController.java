package personal.appointment_ms.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;
import personal.appointment_ms.service.IAppointmentTypeService;

@RestController
@RequestMapping("/appointment-types")
@RequiredArgsConstructor
public class AppointmentTypeController {

    private final IAppointmentTypeService appointmentTypeService;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(
            @RequestBody CreateAppointmentTypeRequest request) {

        AppointmentTypeResponse response =
                appointmentTypeService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> findAll() {

        return ResponseEntity.ok(
                appointmentTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponse> findById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentTypeService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentTypeResponse> update(
            @PathVariable Long id,
            @RequestBody UpdateAppointmentTypeRequest request) {

        return ResponseEntity.ok(
                appointmentTypeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivate(
            @PathVariable Long id) {

        appointmentTypeService.deactivate(id);

        return ResponseEntity.noContent().build();
    }
}