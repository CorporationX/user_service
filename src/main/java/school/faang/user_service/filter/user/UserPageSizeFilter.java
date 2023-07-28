package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageSizeFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPageSize() != 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users;
    }
}
