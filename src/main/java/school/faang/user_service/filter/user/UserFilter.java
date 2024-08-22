package school.faang.user_service.filter.user;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {
    boolean checkingForNull(UserFilterDto userFilterDto);

    boolean filterUsers(User user, UserFilterDto userFilterDto);
}