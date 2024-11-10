package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class MaxExperiencePatternFilter extends UserFilter {
    private int pattern;

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.experienceMax();
        return pattern > 0;
    }

    @Override
    public boolean apply(User user) {
        return user.getExperience() <= pattern;
    }
}
