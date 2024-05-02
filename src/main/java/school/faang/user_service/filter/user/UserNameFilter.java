package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {

    @Override
    public Stream<User> filter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getNamePattern() != null) {
            return users.filter(user -> user.getUsername().startsWith(userFilterDto.getNamePattern()));
        }
        return users;
    }
}
