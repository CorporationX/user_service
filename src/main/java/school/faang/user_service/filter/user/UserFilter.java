package school.faang.user_service.filter.user;

import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFiltersDto userFiltersDto);

    Stream<User> apply(Stream<User> user, UserFiltersDto userFiltersDto);
}
