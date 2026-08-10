package personal.appointment_ms.service;

import java.util.List;

import personal.appointment_ms.dto.AppointmentTypeResponse;
import personal.appointment_ms.dto.CreateAppointmentTypeRequest;
import personal.appointment_ms.dto.UpdateAppointmentTypeRequest;

public interface IAppointmentTypeService {

    AppointmentTypeResponse create(
            CreateAppointmentTypeRequest request);

    List<AppointmentTypeResponse> findAll();

    AppointmentTypeResponse findById(Long id);

    AppointmentTypeResponse update(
            Long id,
            UpdateAppointmentTypeRequest request);

    void deactivate(Long id);
}