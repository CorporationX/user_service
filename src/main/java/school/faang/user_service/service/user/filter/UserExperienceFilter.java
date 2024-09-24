package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

@Component
public class UserExperienceFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserExtendedFilterDto filters) {
        return filters.getExperienceMin() != 0 || filters.getExperienceMax() != 0;
    }

    @Override
    public Predicate<User> getPredicate(UserExtendedFilterDto filters) {
        return (user -> {
            if (filters.getExperienceMax() != 0) {
                return filters.getExperienceMin() <= user.getExperience()
                        && user.getExperience() <= filters.getExperienceMax();
            }
            return filters.getExperienceMin() <= user.getExperience();
        });
    }
}
