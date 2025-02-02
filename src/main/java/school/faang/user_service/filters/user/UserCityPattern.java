package school.faang.user_service.filters.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
public class UserCityPattern implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getCityPattern() != null;
    }

    @Override
    public List<User> apply(List<User> users, UserFilterDto filter) {
        return users.stream().filter(user -> user.getCity().contains(filter.getCityPattern())).toList();
    }
}
