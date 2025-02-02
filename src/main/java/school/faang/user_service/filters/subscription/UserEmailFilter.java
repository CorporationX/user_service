package school.faang.user_service.filters.subscription;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserEmailFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getEmailPattern() != null && !filter.getEmailPattern().isEmpty();
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filter) {
        users.filter(user -> user.getEmail().contains(filter.getEmailPattern()));
    }
}
