package school.faang.user_service.filter.memory.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.filter.memory.InMemoryFilter;

public interface UserInMemoryFilter extends InMemoryFilter<UserDto, UserFilterDto> {
}
