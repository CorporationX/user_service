package school.faang.user_service.filter.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.Filter;

public interface UserFilter extends Filter<User, UserFilterDto> {
}
