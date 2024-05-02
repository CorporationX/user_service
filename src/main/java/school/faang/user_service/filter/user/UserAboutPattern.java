package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserAboutPattern implements UserFilter {

    @Override
    public Stream<User> filter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getAboutPattern() != null) {
            return users.filter(user -> user.getAboutMe().startsWith(userFilterDto.getAboutPattern()));
        }
        return users;
    }
}
