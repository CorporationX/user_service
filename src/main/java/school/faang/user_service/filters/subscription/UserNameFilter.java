package school.faang.user_service.filters.subscription;

import java.util.stream.Stream;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

public class UserNameFilter implements UserFilter {

  @Override
  public boolean isApplicable(UserFilterDto filter) {
    return filter.getNamePattern() != null && !filter.getNamePattern().isEmpty();
  }

  @Override
  public void apply(Stream<User> users, UserFilterDto filter) {
    users.filter(user -> user.getUsername().contains(filter.getNamePattern()));
  }
}
