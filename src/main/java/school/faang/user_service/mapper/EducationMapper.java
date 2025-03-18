package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.education.EducationDto;

@Mapper(componentModel = "spring")
public interface EducationMapper {

    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);

    void updateEducationFromDto(EducationDto dto, @MappingTarget Education entity);

    Education toEducationWithUser(EducationDto educationDto, User user);
}