package personal.doctor_ms.mapper;

import org.mapstruct.Mapper;
import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.entities.Doctor;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    DoctorResponse toResponse(Doctor doctor);

}