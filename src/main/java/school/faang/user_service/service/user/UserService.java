package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;

public interface UserService {
    void deactivateUser(Long id);
}
