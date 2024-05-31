package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
class UserEmailFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getEmailPattern() != null && !filters.getEmailPattern().isBlank();
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(user -> user.getEmail().matches(filters.getEmailPattern()));
    }
}
