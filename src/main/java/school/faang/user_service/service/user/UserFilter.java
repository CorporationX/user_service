package school.faang.user_service.service.user;

import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;

import java.util.function.Predicate;

public interface UserFilter {
    boolean isApplicable(UserExtendedFilterDto filters);

    Predicate<User> getPredicate(UserExtendedFilterDto filters);
}
