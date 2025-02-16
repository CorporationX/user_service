package school.faang.user_service.service.users;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

public interface UsersService {

    User findByIdOrThrow(long userId);

    UserDto getUser(Long userId);

}
