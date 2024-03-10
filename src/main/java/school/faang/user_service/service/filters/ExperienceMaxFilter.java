package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class ExperienceMaxFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getExperienceMin() != 0;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto filters) {
        users.filter(user -> user.getExperience() <= filters.getExperienceMax());
    }
}
