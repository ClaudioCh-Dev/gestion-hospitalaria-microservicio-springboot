package personal.shared.event;

import personal.shared.event.status.StatusAppointment;

public record AppointmentUpdateStatusEvent(
    Long   appointmentId,
    StatusAppointment status,
    Long   doctorId,
    // userId (auth-server) del médico: notification-ms le envía el evento solo a él (y a los admins)
    Long   doctorUserId
) {}
