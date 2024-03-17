package school.faang.user_service.service.filters;

import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.stream.Stream;

public class SkillPatternFilter implements UserFilter {
    @Override
    public boolean isApplicable(UserFilterDto userFilterDto) {
        return userFilterDto.getSkillPattern() != null;
    }

    @Override
    public void apply(Stream<User> users, UserFilterDto userFilterDto) {
        users.filter(user -> user.getSkills().contains(userFilterDto.getSkillPattern()));
    }
}
