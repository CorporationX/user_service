package school.faang.user_service.mapper;

import com.json.student.Education;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.model.dto.EducationDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EducationMapper {
    Education toEntity(EducationDto educationDto);
}