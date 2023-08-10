package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.subscription.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class CountryFilter implements UserFilter {


    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getContactPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getCountry().equals(userFilterDto.getCountryPattern()));
    }
}
