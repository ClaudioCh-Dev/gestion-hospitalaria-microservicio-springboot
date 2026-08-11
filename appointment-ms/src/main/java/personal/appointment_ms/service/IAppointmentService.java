package personal.appointment_ms.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import personal.appointment_ms.dto.AppointmentResponse;
import personal.appointment_ms.dto.CreateAppointmentRequest;
import personal.appointment_ms.dto.UpdateAppointmentStatusRequest;

public interface IAppointmentService {

    AppointmentResponse createAppointment(
            CreateAppointmentRequest request);

    Page<AppointmentResponse> getAppointments(
            Pageable pageable);

    AppointmentResponse getAppointmentById(
            Long id);

    List<AppointmentResponse> getAppointmentsByPatient(
            Long patientId);

    List<AppointmentResponse> getAppointmentsByDoctor(
            Long doctorId);

    AppointmentResponse updateStatus(
            Long id,
            UpdateAppointmentStatusRequest request);

    void cancelAppointment(
            Long id);
}