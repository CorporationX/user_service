package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getAboutPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getAboutMe()
                .toLowerCase()
                .contains(filters.getAboutPattern()
                        .toLowerCase()));
    }
}
