package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import school.faang.user_service.entity.User;
import school.faang.user_service.entity.dto.UserDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MentorshipMapper {
    UserDto toDto(User user);
}
