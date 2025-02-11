package school.faang.user_service.filters.subscription;

import java.util.stream.Stream;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public interface UserFilter {
  boolean isApplicable(UserFilterDto filter);

  void apply(Stream<User> users, UserFilterDto filter);
}
