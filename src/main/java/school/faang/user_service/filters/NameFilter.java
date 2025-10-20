package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class NameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return filters.namePattern() != null && !filters.namePattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users
                .filter(user -> user.getUsername().toLowerCase()
                        .contains(filters.namePattern().toLowerCase()));
    }
}
