package school.faang.user_service.user_filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    void apply(List<User> users, UserFilterDto filters);
}