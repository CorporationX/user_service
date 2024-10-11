package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

import java.util.List;

public interface UserService {
    List<UserDto> getPremiumUsers(UserFilterDto userFilterDto);
    UserDto getUser(long userId);
    List<UserDto> getUsersByIds(List<Long> ids);
    List<UserDto> getFilteredUsers(UserFilterDto userFilterDto);
}
