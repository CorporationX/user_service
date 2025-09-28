package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.user.User;

@Mapper(componentModel = "spring")
public interface MentorshipUserMapper {
    UserDto toMentorshipUserDto(User user);
}
