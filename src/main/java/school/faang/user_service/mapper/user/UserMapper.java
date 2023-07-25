package school.faang.user_service.mapper.user;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDto toDto(User user);

    List<UserDto> toDto(List<User> users);
}
