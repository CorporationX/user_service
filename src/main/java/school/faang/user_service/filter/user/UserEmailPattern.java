package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserEmailPattern implements UserFilter {

    @Override
    public Stream<User> filter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getEmailPattern() != null) {
            return users.filter(user -> user.getEmail().startsWith(userFilterDto.getEmailPattern()));
        }
        return users;
    }
}
