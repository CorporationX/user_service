package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto getUserById(long userId);

    List<UserDto> getUsersByIds(List<Long> ids);

    void banUser(String userIdStr);
}
