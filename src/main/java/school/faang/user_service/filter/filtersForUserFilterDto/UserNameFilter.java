package school.faang.user_service.filter.filtersForUserFilterDto;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.goal.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements DtoUserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> user.getUsername().contains(filterDto.getNamePattern()));
    }
}
