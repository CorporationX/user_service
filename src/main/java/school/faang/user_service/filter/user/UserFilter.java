package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.dto.user.UserFilterDto;
import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto userFilterDto);

    Stream<UserDto> apply(Stream<UserDto> userStream, UserFilterDto userFilterDto);
}
