package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserExtendedFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

@Component
public class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserExtendedFilterDto filters) {
        return filters.getSkillPattern() != null;
    }

    @Override
    public Predicate<User> getPredicate(UserExtendedFilterDto filters) {
        return user -> user.getSkills().stream()
                    .anyMatch(skill -> skill.getTitle() != null && skill.getTitle().contains(filters.getSkillPattern()));
    }
}
