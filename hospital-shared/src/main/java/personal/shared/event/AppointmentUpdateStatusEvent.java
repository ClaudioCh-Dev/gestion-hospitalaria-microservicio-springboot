package personal.shared.event;

public record AppointmentUpdateStatusEvent(
    Long   appointmentId,
    EnumStatusAppointment status,
    String patientName,
    String doctorName
) {}