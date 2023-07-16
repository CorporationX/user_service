package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getAboutPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filters) {
        users.filter(user -> filters.getAboutPattern().contains(user.getAboutMe()));
    }
}
