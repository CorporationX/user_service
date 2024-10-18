package school.faang.user_service.filter;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

public interface UserFilter {
    boolean apply(UserDto user, UserFilterDto filter);
}
