package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

public class UserCityFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return Objects.nonNull(filters.getCityPattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getCity().contains(filters.getCityPattern()));
    }
}
