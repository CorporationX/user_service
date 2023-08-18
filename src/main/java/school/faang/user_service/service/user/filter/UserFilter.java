package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);
    Stream<User> apply(Stream<User> users, UserFilterDto filters);
}
