package personal.shared.event;

import personal.shared.event.status.StatusAppointment;

public record AppointmentUpdateStatusEvent(
    Long   appointmentId,
    StatusAppointment status
) {}