package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

public interface UserService {
    boolean doesUserExist(long userId);

    User getUserById(long userId);

    @Transactional
    UserDto deactivateUser(long userId);

    User findById(long userId);

    void checkUserExists(Long userId);

    boolean existsById(long userId);

    UserDto findUserAndReturnDto(Long userId);

}
