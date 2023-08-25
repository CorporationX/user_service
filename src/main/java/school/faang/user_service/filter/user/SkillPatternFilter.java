package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;
@Component
public class SkillPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto filter) {
        return !(filter.getSkillPattern() == null || filter.getSkillPattern().isEmpty());
    }

    @Override
    public Stream<User> apply(Stream<User> users, UserFilterDto filterDto) {
        return users.filter(user ->
                user.getSkills().stream()
                        .anyMatch(skill -> skill.getTitle()
                                .toLowerCase()
                                .contains(filterDto.getSkillPattern().toLowerCase())
                        )
        );
    }
}
