package personal.doctor_ms.mapper;

import org.mapstruct.Mapper;
import personal.doctor_ms.dtos.SpecialtyResponse;
import personal.doctor_ms.entities.Specialty;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

    SpecialtyResponse toResponse(Specialty specialty);

}