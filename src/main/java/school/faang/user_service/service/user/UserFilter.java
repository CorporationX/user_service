package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.function.Predicate;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    Predicate<User> getPredicate(UserFilterDto filters);
}
