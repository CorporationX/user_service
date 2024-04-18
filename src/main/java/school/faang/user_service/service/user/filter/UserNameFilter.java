package school.faang.user_service.service.user.filter;

import org.springframework.scheduling.annotation.Async;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserNameFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getUserName() != null;
    }

    @Async
    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getUsername().contains(userFilterDto.getUserName()));
    }
}
