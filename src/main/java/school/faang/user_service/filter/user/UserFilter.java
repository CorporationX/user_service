package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public interface UserFilter {
    boolean checkingForNull(UserFilterDto userFilterDto);

    boolean filterUsers(User user, UserFilterDto userFilterDto);
}