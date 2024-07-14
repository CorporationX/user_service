package school.faang.user_service.mapper.mentorship;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.mentorship.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);
    List<UserDto> toDtoList(List<User> userList);
}
