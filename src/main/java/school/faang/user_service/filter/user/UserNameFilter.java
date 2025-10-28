package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class UserNameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFiltersDto userFiltersDto) {
        return StringUtils.hasText(userFiltersDto.namePattern());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto userFiltersDto) {
        return users
                .filter(user -> userFiltersDto.namePattern().equalsIgnoreCase(user.getUsername()));
    }
}
