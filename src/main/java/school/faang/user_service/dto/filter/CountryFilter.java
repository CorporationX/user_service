package school.faang.user_service.dto.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class CountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return filter.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filter) {
        return users.filter(user -> user.getCountry().getTitle().contains(filter.getCountryPattern()));
    }
}
