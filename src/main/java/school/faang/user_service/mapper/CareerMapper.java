package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    Career toCareer(CareerDto careerDto);
    CareerDto toCareerDto(Career career);
}
