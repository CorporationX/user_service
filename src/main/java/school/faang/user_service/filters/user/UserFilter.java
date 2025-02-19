package school.faang.user_service.filters.user;

import java.util.List;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {
  boolean isApplicable(UserFilterDto filter);

  List<User> apply(List<User> users, UserFilterDto filter);
}
