package personal.shared.event;

public record AppointmentCreatedTypeEvent (
     Long id,
     String title,
     String description,
     Boolean active
) {
}
