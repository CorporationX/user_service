package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {
    User getUserById(long userId);

    void validateUserExists(Long userId);

    boolean existsById(long userId);

    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
}
