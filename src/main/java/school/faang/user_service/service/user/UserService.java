package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {

    User findUserById(long id);

    List<UserDto> findPremiumUsers(UserFilterDto filterDto);

    void deactivateUserById(Long id);

    UserDto getUser(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);
}
