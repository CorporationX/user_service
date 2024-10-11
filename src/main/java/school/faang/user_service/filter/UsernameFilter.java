package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

@Component
public class UsernameFilter implements UserFilter {

    @Override
    public boolean apply(UserDto user, UserFilterDto filter) {
        return filter.getNamePattern() == null || user.getUsername().matches(filter.getNamePattern());
    }
}
