package school.faang.user_service.filter.user;

import school.faang.user_service.model.dto.user.UserFilterDto;
import school.faang.user_service.model.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable (UserFilterDto filters);

    Stream<User> apply(Stream<User> users, UserFilterDto filters);
}
