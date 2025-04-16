package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> findUserById(long userId);

    UserDto getUserById(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    void banUser(String userIdStr);
}
