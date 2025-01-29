package school.faang.user_service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.dto.entity.User;
import java.util.stream.Stream;

public class CountryPatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getCountryPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filterDto) {
        users.filter(user -> user.getCountry().equals(filterDto.getCountryPattern()));
    }

}
