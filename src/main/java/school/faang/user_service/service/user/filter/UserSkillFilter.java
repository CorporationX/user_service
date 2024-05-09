package school.faang.user_service.service.user.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

@Component
class UserSkillFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getSkillPattern() != null && !filters.getSkillPattern().isBlank();
    }

    @Override
    public Stream<User> apply(List<User> users, UserFilterDto filters) {
        return users.stream()
                .filter(user -> {
                    var matchedSkillsList = user.getSkills().stream()
                            .map(Skill::getTitle)
                            .filter(skill -> skill.matches(filters.getSkillPattern()))
                            .toList();

                    return matchedSkillsList.size() > 0;
                });
    }
}
