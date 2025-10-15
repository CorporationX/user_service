package school.faang.user_service.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import school.faang.user_service.dto.user.CreateEducationDto;
import school.faang.user_service.dto.user.EducationDto;
import school.faang.user_service.dto.education.UpdateEducationDto;
import school.faang.user_service.entity.user.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    @Mapping(target = "user", ignore = true)
    Education toEducation(CreateEducationDto dto);

    EducationDto toEducationDto(Education entity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "user", ignore = true)
    void update(@MappingTarget Education target, UpdateEducationDto dto);
}