package school.faang.user_service.mapper.education;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.EducationDto.AddEducationDto;
import school.faang.user_service.dto.EducationDto.EducationDto;
import school.faang.user_service.dto.EducationDto.EducationUpdateDto;
import school.faang.user_service.entity.Education;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    EducationMapper INSTANCE = Mappers.getMapper(EducationMapper.class);

    Education toEducation(EducationUpdateDto educationUpdateDto);
    Education toEducation(AddEducationDto addEducationDto);
    EducationDto toEducationDto(Education education);
}