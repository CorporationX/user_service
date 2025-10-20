package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;

@Mapper(componentModel = "spring")
public interface CareerMapper {
    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(CreateCareerDto createCareerDto);

    @Mapping(source = "from", target = "dateFrom")
    @Mapping(source = "to", target = "dateTo")
    Career toCareer(UpdateCareerDto updateCareerDto);

    @Mapping(source = "dateFrom", target = "from")
    @Mapping(source = "dateTo", target = "to")
    CareerDto toCareerDto(Career career);

}
