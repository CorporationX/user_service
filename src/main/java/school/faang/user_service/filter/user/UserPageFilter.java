package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPageFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getPage();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.skip((long) filters.getPage() * filters.getPageSize());
    }
}
