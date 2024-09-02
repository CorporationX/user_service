package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserEmailFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter != null && filter.getEmailPattern() != null;
    }

    @Override
    public boolean apply(User user, UserFilterDto filter) {
        return user.getEmail().matches(filter.getEmailPattern());
    }
}