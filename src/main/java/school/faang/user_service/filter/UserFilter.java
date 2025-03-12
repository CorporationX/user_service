package school.faang.user_service.filter;

import school.faang.user_service.dto.UserFilterRequest;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterRequest userFilterRequest);

    Stream<User> apply(Stream<User> users, UserFilterRequest userFilterRequest);
}
