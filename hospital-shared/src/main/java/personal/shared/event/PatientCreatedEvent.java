package personal.shared.event;

public record PatientCreatedEvent (
    Long id,
    String firstName,
    String lastName,
    String email
){}