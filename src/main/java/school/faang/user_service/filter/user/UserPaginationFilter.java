package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPaginationFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return (userFilterDto.getPage() >= 0 && userFilterDto.getPageSize() > 0);
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        int page = userFilterDto.getPage();
        int pageSize = userFilterDto.getPageSize();
        int startIdx = page * pageSize;
        return users.skip(startIdx).limit(pageSize);
    }
}
