package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.CareerDto;
import school.faang.user_service.entity.Career;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CareerMapper {

        @Mapping(target = "dateFrom", source = "from")
        @Mapping(target = "dateTo", source = "to")
        @Mapping(target = "user", ignore = true)
        Career toCareer(CareerDto careerDto);

        @Mapping(target = "from", source = "dateFrom")
        @Mapping(target = "to", source = "dateTo")
        CareerDto toCareerDto(Career career);
}
