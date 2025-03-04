package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.phonePattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getPhone().matches(userFilterDto.phonePattern()));
    }
}
