package school.faang.user_service.service.subscription.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import java.util.stream.Stream;

@Component
public class ExperienceMax implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.experienceMax() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getExperience() <= filters.experienceMax());
    }
}
