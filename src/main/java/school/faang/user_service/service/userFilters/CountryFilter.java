package school.faang.user_service.service.userFilters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

@Component
public class CountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getCountryPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(filter -> filter.getCountry().getTitle().contains(userFilterDto.getCountryPattern()));
    }
}
