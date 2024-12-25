package school.faang.user_service.filter.userFilter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.model.Skill;
import school.faang.user_service.model.User;

@Component
public class SkillsPatternFilter extends UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto filters) {
        this.pattern = filters.skillPattern();
        return pattern != null;
    }

    @Override
    public boolean apply(User user) {
        return user.getSkills() != null && user.getSkills().stream()
                .map(Skill::getTitle)
                .anyMatch(skill -> skill.contains(pattern));
    }
}
