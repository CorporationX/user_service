package school.faang.user_service.education_addition;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.entity.Education;
import school.faang.user_service.entity.User;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public class EducationMapper {

    EducationDto toDto(Education education) {
        return null;
    }

    Education toEntity(Education existingEducation, User educationDto) {
        return null;
    }
}