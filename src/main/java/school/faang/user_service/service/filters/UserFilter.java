package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    void apply(Stream<User> users, UserFilterDto userFilterDto);
}
