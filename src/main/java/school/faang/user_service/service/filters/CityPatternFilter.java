package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class CityPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getCityPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto userFilterDto) {
        users.filter(user -> user.getCity().matches(userFilterDto.getCityPattern()));
    }
}
