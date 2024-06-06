package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserExperienceSpanFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        int experienceMin = filters.getExperienceMin();
        int experienceMax = filters.getExperienceMax();

        return (experienceMin != 0 || experienceMax != 0) && (experienceMin <= experienceMax || experienceMax == 0);
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> {
            if (filters.getExperienceMax() == 0) {
                return user.getExperience() >= filters.getExperienceMin();
            } else {
                return user.getExperience() >= filters.getExperienceMin() && user.getExperience() <= filters.getExperienceMax();
            }
        });
    }
}
