package school.faang.user_service.filter.filtersForUserFilterDto;

import org.springframework.stereotype.Component;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.goal.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserCityFilter implements DtoUserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getCityPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> user.getCity().contains(filterDto.getCityPattern()));
    }
}
