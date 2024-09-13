package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserEmailFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null && filter.getEmailPattern() != null;
    }

    @Override
    public boolean apply(User user, UserFilterDto filter) {
        return user != null && user.getEmail() != null &&
                user.getEmail().matches(filter.getEmailPattern());
    }
}