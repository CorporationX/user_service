package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Component
public class UserExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperienceMax() != 0;
    }

    @Override
    public void apply(List<User> users, UserFilterDto filters) {
        users.removeIf(user -> user.getExperience() > filters.getExperienceMax());
    }
}
