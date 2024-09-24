package school.faang.user_service.service.userClient;

import school.faang.user_service.dto.user.UserDto;

import java.util.List;

public interface UserClientService {
    UserDto getUser(long id);

    List<UserDto> getUsersByIds(List<Long> ids);
}
