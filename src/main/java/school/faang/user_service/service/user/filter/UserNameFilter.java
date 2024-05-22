package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream().filter(user -> user.getUsername().contains(filters.getNamePattern()));
    }
}
