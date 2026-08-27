package personal.shared.event;

public record PatientUpdateEvent( 
    Long id,
    String firstName,
    String lastName,
    String email
){}