package school.faang.user_service.service.subscription.filter;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class NameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.namePattern() != null && !filters.namePattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> Objects.equals(user.getUsername(), filters.namePattern()));
    }
}
