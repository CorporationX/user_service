package school.faang.user_service.filter;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.UserFilterDto;

public interface UserFilter {
    boolean apply(UserDto user, UserFilterDto filter);
}
