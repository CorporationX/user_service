package school.faang.user_service.filters.user;

import java.util.List;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class UserCityPattern implements UserFilter {
  @Override
  public boolean isApplicable(UserFilterDto filter) {
    return filter.getCityPattern() != null;
  }

  @Override
  public List<User> apply(List<User> users, UserFilterDto filter) {
    return users.stream().filter(user -> user.getCity().contains(filter.getCityPattern())).toList();
  }
}
