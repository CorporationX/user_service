package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.education.CreateEducationDto;
import school.faang.user_service.dto.user.education.EducationDto;
import school.faang.user_service.entity.user.Education;



@Mapper(componentModel = "spring")
public interface EducationMapper {
    Education toEducation(CreateEducationDto educationDto);

    EducationDto toEducationDto(Education education);
}