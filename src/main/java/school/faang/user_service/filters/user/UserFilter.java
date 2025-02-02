package school.faang.user_service.filters.user;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filter);

    List<User> apply(List<User> users, UserFilterDto filter);
}
