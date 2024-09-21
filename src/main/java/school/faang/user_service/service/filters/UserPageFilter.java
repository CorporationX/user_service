package school.faang.user_service.service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.Comparator;
import java.util.stream.Stream;

@Component
public class UserPageFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getPage() > 0 && userFilterDto.getPageSize() > 0;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto filterDto) {
        int page = filterDto.getPage();
        int size = filterDto.getPageSize();

        return userStream.sorted(Comparator.comparing(User::getCreatedAt))
                .skip(page  * size);
    }
}
