package school.faang.user_service.service;

import school.faang.user_service.model.dto.user.UserDto;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface UserService {

    List<UserDto> getPremiumUsers(UserFilterDto filter);

    void deactivateUserProfile(long id);

    void addUsersFromFile(InputStream fileStream) throws IOException;

    User findUserById(long userId);

    UserDto getUser(long userId);
    UserDto getUserProfile(long userId);

    List<UserDto> getUsersByIds(List<Long> userIds);

    void banUserById(Long id);
}
