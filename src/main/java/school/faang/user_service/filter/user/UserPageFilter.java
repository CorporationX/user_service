package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPage() < 0;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.skip((long) filters.getPage() * filters.getPageSize()).limit(filters.getPageSize());
    }
}
