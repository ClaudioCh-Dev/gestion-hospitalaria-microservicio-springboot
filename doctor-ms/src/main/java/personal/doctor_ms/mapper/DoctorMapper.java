package personal.doctor_ms.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import personal.doctor_ms.dtos.DoctorResponse;
import personal.doctor_ms.entities.Doctor;

@Mapper(componentModel = "spring")
public interface DoctorMapper {

    @Mapping(target = "specialtyId", source = "specialty.id")
    @Mapping(target = "specialtyName", source = "specialty.name")
    DoctorResponse toResponse(Doctor doctor);
}