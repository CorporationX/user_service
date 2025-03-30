package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User getReferenceById(long userId);

    boolean doesUserExist(long userId);

    User getUserById(long userId);

    User findById(long userId);

    void checkUserExists(Long userId);

    boolean existsById(long userId);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);
}
