package school.faang.user_service.service;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserEmailFilter implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getEmailPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filterDto) {
        users.filter(user -> user.getEmail().contains(filterDto.getEmailPattern()));
    }
}
