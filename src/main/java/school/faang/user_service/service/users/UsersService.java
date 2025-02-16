package school.faang.user_service.service.users;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.user.UserResponseDto;
import school.faang.user_service.entity.User;

public interface UsersService {

    User findByIdOrThrow(long userId);

    UserResponseDto getUser(Long userId);

}
