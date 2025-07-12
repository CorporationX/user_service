package school.faang.user_service.education_addition.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.education_addition.EducationDto;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {

    Education toEntity(EducationDto dto);
    Education toEntity(EducationDto dto , User user );
    EducationDto toDto(Education education);
}