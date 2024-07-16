package school.faang.user_service.service.filter.userFilter;

import school.faang.user_service.dto.userDto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplication(UserFilterDto userFilterDto);

    Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto);
}
