package school.faang.user_service.service.user.filter;

import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class UserSkillFilter implements UserFilter{
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getSkillPattern() != null && !filters.getSkillPattern().isBlank();
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> {
            var matchedSkillsList = user.getSkills().stream()
                    .map(Skill::getTitle)
                    .filter(skill -> skill.matches(filters.getSkillPattern()))
                    .toList();

            return matchedSkillsList.size() > 0;
        });
    }
}
