package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

class UserExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return (filters.getExperienceMin() != 0 || filters.getExperienceMax() != 0)
                && (filters.getExperienceMin() >= 0 && filters.getExperienceMax() >= 0);
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> {
            var userExperience = user.getExperience();

            return !(userExperience < filters.getExperienceMin() || (userExperience > filters.getExperienceMax() && filters.getExperienceMax() > 0));
        });
    }
}
