package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;

import java.util.List;

public interface UserService {

    void deactivateUser(Long id);

    List<UserDto> getUsersByIds(List<Long> ids);

    UserDto getUser(long id);
}
