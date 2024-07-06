package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto userFilterDto);

    List<User> apply(List<User> users, UserFilterDto userFilter);
}
