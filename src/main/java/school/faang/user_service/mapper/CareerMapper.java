package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.entity.user.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {

    Career toEntity(CareerDto careerDto);

    CareerDto toDto(Career career);

}
