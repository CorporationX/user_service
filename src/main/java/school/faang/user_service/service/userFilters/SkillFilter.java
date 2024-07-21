package school.faang.user_service.service.userFilters;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.filter.UserFilter;

import java.util.stream.Stream;

@Component
public class SkillFilter implements UserFilter {

    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getSkillPattern() != null;
    }

    @Override
    public Stream<User> apply(Stream<User> userStream, UserFilterDto userFilterDto) {
        return userStream.filter(user -> user.getSkills().stream()
                .anyMatch(skill -> skill.getTitle().contains(userFilterDto.getSkillPattern())));
    }
}
