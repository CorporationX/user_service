package school.faang.user_service.service.user.filter;

import org.springframework.scheduling.annotation.Async;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getCountryName() != null;
    }

    @Async
    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto userFilterDto) {
        return users.filter(user -> user.getCountry().getTitle().contains(userFilterDto.getCountryName()));
    }
}
