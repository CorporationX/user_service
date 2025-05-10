package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import school.faang.user_service.dto.EducationDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);
}
