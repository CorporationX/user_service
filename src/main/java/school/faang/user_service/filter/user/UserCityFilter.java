package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
class UserCityFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getCityPattern() != null && !userFilterDto.getCityPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.peek(u -> System.out.println("city " + u)).filter(user -> user.getCity().contains(userFilterDto.getCityPattern())).peek(u -> System.out.println("city " + u));
    }
}
