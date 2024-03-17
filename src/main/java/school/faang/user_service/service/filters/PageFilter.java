package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class PageFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getPage() != 0 && filters.getPageSize() != 0;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto userFilterDto) {
        users.skip(userFilterDto.getPage() * userFilterDto.getPageSize());
    }
}
