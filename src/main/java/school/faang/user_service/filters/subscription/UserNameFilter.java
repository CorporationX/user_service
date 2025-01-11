package school.faang.user_service.filters.subscription;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserNameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getNamePattern() != null && !filter.getNamePattern().isEmpty();
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filter) {
        users.filter(user -> user.getUsername().contains(filter.getNamePattern()));
    }
}
