package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;

import java.util.stream.Stream;

@Component
public class UserUsernameFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filterDto) {
        return filterDto.getUsername() != null && !filterDto.getUsername().isEmpty();
    }

    @Override
    public Stream<UserDto> apply(Stream<UserDto> stream, UserFilterDto filterDto) {
        return stream.filter(userDto -> userDto.getUsername().equals(filterDto.getUsername()));
    }

}
