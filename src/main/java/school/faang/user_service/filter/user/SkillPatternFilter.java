package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

@Component
public class SkillPatternFilter implements UserFilter {
    @Override
    public boolean apply(User user, UserFilterDto filter) {
        return filter.getSkillPattern() == null || user.getSkills().stream().anyMatch(skill -> skill.getTitle().contains(filter.getSkillPattern()));
    }
}
