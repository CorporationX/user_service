package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserEmailFilter implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getEmailPattern() != null;
    }

    @Override
    public void apply(List<User> users, UserFilterDto filters) {
        users.removeIf(user -> !user.getEmail().contains(filters.getEmailPattern()));
    }
}
