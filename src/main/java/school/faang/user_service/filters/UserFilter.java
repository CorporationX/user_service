package school.faang.user_service.filters;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

public interface UserFilter {

    boolean isApplicable(UserFiltersDto filters);

    Stream<User> apply(Stream<User> users, UserFiltersDto filters);
}
