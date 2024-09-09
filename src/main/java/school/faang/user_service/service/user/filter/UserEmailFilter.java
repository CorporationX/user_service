package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserEmailFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getEmail() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> eventStream, UserFilterDto filter) {
        return eventStream.filter(event -> event.getEmail().contains(filter.getEmail()));
    }
}
