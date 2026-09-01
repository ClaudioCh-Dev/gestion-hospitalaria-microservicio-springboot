package personal.shared.event;

public record PatientCreateEvent( 
    Long id,
    String firstName,
    String lastName,
    String email
){}