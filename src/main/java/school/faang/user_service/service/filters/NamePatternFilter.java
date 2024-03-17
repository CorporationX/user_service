package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class NamePatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getNamePattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto userFilterDto) {
        users.filter(user -> user.getUsername().matches(userFilterDto.getNamePattern()));
    }
}
