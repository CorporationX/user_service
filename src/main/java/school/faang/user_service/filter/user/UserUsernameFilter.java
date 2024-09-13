package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserUsernameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null && filter.getNamePattern() != null;
    }

    @Override
    public boolean apply(User user, UserFilterDto filter) {
        return user != null && user.getUsername() != null &&
                user.getUsername().matches(filter.getNamePattern());
    }
}
