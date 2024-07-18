package school.faang.user_service.service.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserNameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getUsername().contains(filters.getNamePattern()));
    }
}
