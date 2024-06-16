package school.faang.user_service.service.user;

import org.springframework.web.bind.annotation.PathVariable;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserService {

    UserDto createUser(UserDto userDto);

    User findUserById(long id);

    List<UserDto> findPremiumUsers(UserFilterDto filterDto);

    void deactivateUserById(Long id);

    List<UserDto> getUsersByIds(List<Long> ids);

    UserDto getUserById(long userId);
}
