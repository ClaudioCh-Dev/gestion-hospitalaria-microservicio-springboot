package personal.doctor_ms.dtos;

import jakarta.validation.constraints.*;
import java.time.LocalTime;

public record CreateDoctorRequest(

        @NotBlank(message = "El número de licencia es obligatorio")
        @Size(max = 30, message = "La licencia no puede superar 30 caracteres")
        String licenseNumber,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100)
        String firstName,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(max = 100)
        String lastName,

        @Email(message = "El email no tiene formato válido")
        @Size(max = 150)
        String email,

        @Size(max = 20)
        String phone,

        @NotNull(message = "La especialidad es obligatoria")
        Long specialtyId,

        LocalTime scheduleStart,

        LocalTime scheduleEnd

) {
}