package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import school.faang.user_service.entity.dto.education.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    @Mapping(target = "user", ignore = true)
    Education toEducation(EducationDto educationDto);

    EducationDto toEducationDto(Education education);

    @Mapping(target = "user", ignore = true)
    void updateEducationFromDto(EducationDto dto, @MappingTarget Education entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", source = "user")
    Education toEducationWithUser(EducationDto educationDto, User user);
}