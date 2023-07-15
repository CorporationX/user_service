package school.faang.user_service.mapper;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);
}
