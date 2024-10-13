package school.faang.user_service.mapper;

import com.json.student.Education;
import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.EducationDto;

@Mapper(componentModel = "spring")
public interface EducationMapper {
    Education toEntity(EducationDto educationDto);
}