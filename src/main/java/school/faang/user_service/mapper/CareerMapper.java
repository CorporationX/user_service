package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.career.CareerDto;
import school.faang.user_service.dto.career.CreateCareerDto;
import school.faang.user_service.dto.career.UpdateCareerDto;
import school.faang.user_service.entity.user.Career;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CareerMapper {

    @Mappings({
            @Mapping(source = "from", target = "dateFrom"),
            @Mapping(source = "to", target = "dateTo")
    })
    Career toCareer(CreateCareerDto careerDto);

    void update(UpdateCareerDto careerDto, @MappingTarget Career entity);

    @Mappings({
            @Mapping(source = "dateFrom", target = "from"),
            @Mapping(source = "dateTo", target = "to")
    })
    CareerDto toCareerDto(Career career);
}