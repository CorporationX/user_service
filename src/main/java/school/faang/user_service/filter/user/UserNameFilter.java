package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getNamePattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return user.getUsername().contains(filters.getNamePattern());
    }
}
