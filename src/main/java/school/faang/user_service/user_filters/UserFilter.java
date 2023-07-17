package school.faang.user_service.user_filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public interface UserFilter {
    boolean isApplicable(UserFilterDto filters);

    void apply(List<User> users, UserFilterDto filters);
}
