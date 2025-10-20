package school.faang.user_service.filters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFiltersDto;
import school.faang.user_service.entity.user.User;

import java.util.stream.Stream;

@Component
public class ExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFiltersDto filters) {
        return filters.experienceMin() > 0 && filters.experienceMax() <= Integer.MAX_VALUE;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFiltersDto filters) {
        return users
                .filter(user -> user.getExperience() >= filters.experienceMin()
                        && user.getExperience() <= filters.experienceMax());
    }
}