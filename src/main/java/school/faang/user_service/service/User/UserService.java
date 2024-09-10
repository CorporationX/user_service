package school.faang.user_service.service.User;

import school.faang.user_service.dto.user.UserDto;

public interface UserService {
    UserDto deactivateUser(Long id);
}
