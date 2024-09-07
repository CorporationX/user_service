package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserPageSizeFilter implements UserFilter {
    private static final int DEFAULT_PAGE_SIZE = 15;

    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getPageSize() >= 0;
    }

    @Override
    public void apply(Stream<User> userStream, UserFilterDto filterDto) {
        int pageSize = (filterDto.getPageSize() > 0) ? filterDto.getPageSize() : DEFAULT_PAGE_SIZE;
        userStream.limit(pageSize);
    }
}
