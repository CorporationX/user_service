package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class CityPatternFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return !(filter.getCityPattern() == null || filter.getCityPattern().isEmpty());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user -> {
            String userCity = user.getCity() != null ? user.getCity() : "";
            return userCity.toLowerCase().contains(filterDto.getCityPattern().toLowerCase());
        });
    }
}
