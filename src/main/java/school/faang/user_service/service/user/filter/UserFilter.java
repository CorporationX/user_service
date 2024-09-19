package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

public interface UserFilter {

    boolean isApplicable(UserFilterDto filterDto);

    List<User> filterUsers(List<User> users, UserFilterDto filterDto);
}
