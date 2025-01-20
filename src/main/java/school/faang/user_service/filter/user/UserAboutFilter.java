package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserAboutFilter extends UserFilter {
    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getAboutPattern();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user ->
                Objects.requireNonNullElse(user.getAboutMe(), "")
                        .contains(filters.getAboutPattern()));
    }
}
