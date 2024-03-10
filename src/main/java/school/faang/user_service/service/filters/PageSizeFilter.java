package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class PageSizeFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPageSize() != 0;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filters) {
        users.limit(filters.getPageSize());
    }
}
