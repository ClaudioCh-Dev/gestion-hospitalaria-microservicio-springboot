package personal.doctor_ms.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public record UpdateDoctorRequest(

        @NotBlank(message = "El nombre es obligatorio")
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        String lastName,

        @Email(message = "El email no tiene formato válido")
        String email,

        @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
        String phone,

        @NotNull(message = "La especialidad es obligatoria")
        Long specialtyId,

        LocalTime scheduleStart,

        LocalTime scheduleEnd,

        Boolean active

) {
}