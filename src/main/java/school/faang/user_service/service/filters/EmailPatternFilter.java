package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class EmailPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getEmailPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto userFilterDto) {
        users.filter(user -> user.getEmail().matches(userFilterDto.getEmailPattern()));
    }
}
