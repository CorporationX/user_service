package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePatter() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filters) {
        users.filter(user -> filters.getNamePatter().contains(user.getUsername()));
    }
}
