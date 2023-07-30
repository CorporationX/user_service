package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getAboutPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getAboutMe().contains(userFilterDto.getAboutPattern()));
    }
}
