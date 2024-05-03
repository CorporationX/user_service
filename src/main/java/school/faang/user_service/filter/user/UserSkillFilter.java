package school.faang.user_service.filter.user;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.user.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

@Component
public class UserSkillFilter implements UserFilter {

    @Override
    @Transactional
    public Stream<User> applyFilter(Stream<User> users, UserFilterDto userFilterDto) {
        if (userFilterDto.getSkillPattern() != null) {
            return users.filter(user -> user.getSkills().stream()
                    .anyMatch(skill -> skill.getTitle().startsWith(userFilterDto.getSkillPattern())));
        }
        return users;
    }
}
