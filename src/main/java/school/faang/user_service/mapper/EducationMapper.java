package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.education.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);
}
