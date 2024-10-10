package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

import java.util.List;

public interface UserService {
    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
    UserDto getUser(long userId);
    List<UserDto> getUsersByIds(List<Long> ids);
}
