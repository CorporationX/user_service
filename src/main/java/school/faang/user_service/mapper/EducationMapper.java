package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;


@Mapper
public interface EducationMapper {

    @Mapping(target = "user", ignore = true)
    Education toEducation(EducationDto educationDto);

    @Mapping(target = "user", ignore = true)
    void updateEducationFromDto(EducationDto educationDto, @MappingTarget Education education);

    EducationDto toEducationDto(Education education);
}