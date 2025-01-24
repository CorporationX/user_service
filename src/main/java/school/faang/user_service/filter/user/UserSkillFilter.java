package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

@Component
public class UserSkillFilter extends UserFilter {

    @Override
    public Object getFilterFieldValue(UserFilterDto filters) {
        return filters.getSkillPattern();
    }

    @Override
    public boolean apply(User user, UserFilterDto filters) {
        return Objects.requireNonNullElse(user.getSkills(), Collections.<Skill>emptyList()).stream()
                .anyMatch(skill ->
                        skill.getTitle().contains(filters.getSkillPattern()));
    }
}
