package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {

    boolean isApplicable(UserFilterDto filterDto);

    boolean test(User user, UserFilterDto filterDto);
}
