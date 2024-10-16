package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.filter_dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

@Component
public class UserCreatedBeforeFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getCreatedBefore() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(g -> g.getCreatedAt().isBefore(filters.getCreatedBefore()));
    }
}
