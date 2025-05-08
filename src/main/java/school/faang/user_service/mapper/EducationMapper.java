package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper
public interface EducationMapper {
    Education toEducation(EducationDto educationDto);
    EducationDto toEducationDto(Education education);

    void updateEducationFromDto(EducationDto educationDto, @MappingTarget Education education);
}