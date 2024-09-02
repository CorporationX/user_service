package school.faang.user_service.filters.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {

    boolean isApplicable(UserFilterDto userFilterDto);

    Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto);
}
