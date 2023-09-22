package school.faang.user_service.filter.user_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    void apply(List<User> users, UserFilterDto filters);
}