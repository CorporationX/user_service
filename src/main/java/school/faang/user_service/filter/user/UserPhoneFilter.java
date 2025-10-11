package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserPhoneFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto userFiltersDto) {
        return userFiltersDto.phonePattern() != null && !userFiltersDto.phonePattern().isEmpty();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto userFiltersDto) {
        return users
                .filter(user -> Objects.equals(userFiltersDto.phonePattern(), user.getPhone()));
    }
}
