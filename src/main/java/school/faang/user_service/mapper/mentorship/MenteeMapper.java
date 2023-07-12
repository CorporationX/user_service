package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.MenteeDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface MenteeMapper {
    MenteeDto toDto(User user);
}
