package school.faang.user_service.service.filters.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class UserSkillPattern implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filters) {
        return filters.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filters) {
        return users.filter(user -> user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle().equals(filters.getSkillPattern())));
    }
}
