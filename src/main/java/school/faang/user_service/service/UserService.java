package school.faang.user_service.service;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface UserService {

    void deactivateUser(Long id);

    UserDto getUser(long id);

    List<UserDto> getUsersByIds(List<Long> ids);

    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
}
