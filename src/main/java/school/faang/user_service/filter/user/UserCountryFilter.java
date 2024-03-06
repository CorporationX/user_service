package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import java.util.stream.Stream;
@Component
public class UserCountryFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getCountryId() != null && userFilterDto.getCountryId().longValue() > 0;
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(userDto -> userDto.getCountryId().equals(userFilterDto.getCountryId()));
    }
}
